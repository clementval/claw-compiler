# This file is released under terms of BSD license
# See LICENSE file for more information

# This CMakeLists.txt file is the entry point for the configuration and the
# compilation of the CLAW Compiler.

cmake_minimum_required(VERSION 3.0)

project("CLAW Compiler" VERSION 2.0.2)

# Add local cmake modules
list(APPEND CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake/module")

# Enable needed modules and languages
enable_language(Fortran)
enable_testing()

find_package(Java 1.8 REQUIRED)
# Warning!!! After finding the first version, find_package(Java) does NOT look for others. CMAKE_FIND_PACKAGE_SORT_ORDER
# and CMAKE_FIND_PACKAGE_SORT_DIRECTION have no effect. To override the "default" java version, set cmake variable
# JAVA_HOME to desired java installation path.

if(Java_VERSION_MAJOR EQUAL 1)
  if(Java_VERSION_MINOR LESS 8)
    message(FATAL_ERROR "Java ${Java_VERSION_STRING} is not supported")
  endif()
else()
  if((Java_VERSION_MAJOR LESS 8) OR (Java_VERSION_MAJOR GREATER 13) )
    message(FATAL_ERROR "Java ${Java_VERSION_STRING} is not supported")
  endif()
endif()

find_package(Ant 1.8 REQUIRED)

include(ExternalProject)
include(cmake/git.cmake)
include(cmake/omni_compiler.cmake)

option(BUILD_OMNI_XCODEML_TOOLS "Build OMNI Compiler Tools" ON)
option(ADD_OMNI_XCODEML_TOOLS_TO_INSTALL "Copy OMNI Compiler Tools to CLAW install directory" ON)
# Load all variables from file
include(properties.cmake)

if(OFFLINE)
  message(STATUS "CLAW Compiler offline compilation enabled")
endif()

# Recover commit hash for the top repositiory
git_get_rev_hash(${CMAKE_SOURCE_DIR} CLAWFC_GIT_HASH)
message(STATUS "CLAW Compiler version ${CLAWFC_GIT_HASH}")

# Set the default install directory
if(CMAKE_INSTALL_PREFIX_INITIALIZED_TO_DEFAULT)
  set(
    CMAKE_INSTALL_PREFIX "/usr/local"
    CACHE PATH "default install path" FORCE
  )
endif(CMAKE_INSTALL_PREFIX_INITIALIZED_TO_DEFAULT)

# Set ant to be verbose
if(ANT_VERBOSE)
  set(ANT_FLAGS "-v")
endif()

# Define preprocessor information used in the driver and tests
# The driver uses preprocessed files only but the tests compile the code.
if("${CMAKE_Fortran_COMPILER_ID}" MATCHES "Cray")
  include(compiler/cray.cmake)
elseif("${CMAKE_Fortran_COMPILER_ID}" MATCHES "PGI")
  include(compiler/pgi.cmake)
elseif("${CMAKE_Fortran_COMPILER_ID}" MATCHES "Intel")
  include(compiler/intel.cmake)
elseif("${CMAKE_Fortran_COMPILER_ID}" MATCHES "NAG")
  include(compiler/nag.cmake)
elseif("${CMAKE_Fortran_COMPILER_ID}" MATCHES "GNU")
  include(compiler/gnu.cmake)
else()
message(
  FATAL_ERROR
  "Unsupported Fortran compiler ${CMAKE_Fortran_COMPILER_ID}"
)
endif()

message(
  STATUS
  "Configure preprocessor with ${CMAKE_Fortran_COMPILER_ID} compiler"
)

add_custom_target(create_int_install_dir ALL
    COMMAND ${CMAKE_COMMAND} -E make_directory ${INT_CLAW_HOME})

install(
    DIRECTORY ${INT_CLAW_HOME}/
    DESTINATION ${CMAKE_INSTALL_PREFIX}
    FILE_PERMISSIONS OWNER_EXECUTE OWNER_WRITE OWNER_READ GROUP_EXECUTE GROUP_READ)
install(CODE "execute_process(COMMAND ${Ant_EXECUTABLE} -f ${ANT_SOLUTION} ${ANT_FLAGS} -Dclaw.properties.dir=${CX2T_PROPERTIES_DIR} install-driver)"
)

option(OMNI_LINK_STATIC_GNU_LIBSTDCXX "OMNI Link static libstdc++ when building OMNI with GNU compiler" OFF)

# Build OMNI compiler
if(BUILD_OMNI_XCODEML_TOOLS)
  set(OMNI_COMPILER_SRC_DIR ${CMAKE_SOURCE_DIR}/xcodeml-tools)
  set(OMNI_COMPILER_BUILD_DIR ${CMAKE_BINARY_DIR}/omni-compiler-build)
  set(OMNI_CMAKE_ARGS -S ${OMNI_COMPILER_SRC_DIR} -DCMAKE_INSTALL_PREFIX=${OMNI_HOME} -DCMAKE_BUILD_TYPE=Release)
  if(DEFINED JAVA_HOME)
    set(OMNI_CMAKE_ARGS ${OMNI_CMAKE_ARGS} -DJAVA_HOME=${JAVA_HOME})
  endif()
  if(${OMNI_LINK_STATIC_GNU_LIBSTDCXX})
    set(OMNI_CMAKE_ARGS ${OMNI_CMAKE_ARGS} -DLINK_STATIC_GNU_LIBSTDCXX=${OMNI_LINK_STATIC_GNU_LIBSTDCXX})
  endif()
  ExternalProject_Add(
    omni-compiler
    SOURCE_DIR ${OMNI_COMPILER_SRC_DIR}
    BINARY_DIR ${OMNI_COMPILER_BUILD_DIR}
    INSTALL_DIR ${OMNI_HOME}
    CONFIGURE_COMMAND
      CC=${CMAKE_C_COMPILER}
      CXX=${CMAKE_CXX_COMPILER}
      ${CMAKE_COMMAND} ${OMNI_CMAKE_ARGS}
    BUILD_COMMAND make -j build-cpp-install build-jni-install
    BUILD_IN_SOURCE OFF
  )
else()
  function(get_xcodemltools_version_tag _output_version_tag)
      execute_process(
        COMMAND ${OMNI_F_FRONT} --version-tag
        ERROR_VARIABLE output_version_tag
        OUTPUT_VARIABLE output_version_tag
        RESULT_VARIABLE return_code
        OUTPUT_STRIP_TRAILING_WHITESPACE
      )
      if(NOT(${return_code} EQUAL "0"))
            message(FATAL_ERROR "get_omni_version_tag failed")
      endif()
      set(${_output_version_tag} ${output_version_tag} PARENT_SCOPE)
  endfunction()
  get_xcodemltools_version_tag(xcodemltools_version)
  if(NOT (${xcodemltools_version} STREQUAL ${OMNI_VERSION_TAG}))
    message(FATAL_ERROR "OMNI XCodeMLTools version tag \"${xcodemltools_version}\" does not match expected \"${OMNI_VERSION_TAG}\"")
  endif()
  add_custom_target(omni-compiler
    COMMENT "Checking OMNI XCodeMLTools version"
  )
endif(BUILD_OMNI_XCODEML_TOOLS)

# translator library and xcodeml manipulation library
add_subdirectory(cx2t)

add_custom_target(build-claw-solution ALL)
# compiler driver
add_subdirectory(driver)

add_custom_target(generate-xmods)
# generate generic .xmod files
add_subdirectory(modules)

add_subdirectory(tests_runner)

# Build documentation
add_subdirectory(documentation)

set(ADDITIONAL_CLEAN_FILES "")
LIST(APPEND ADDITIONAL_CLEAN_FILES ${INT_CLAW_HOME})
SET_DIRECTORY_PROPERTIES(PROPERTIES ADDITIONAL_MAKE_CLEAN_FILES "${ADDITIONAL_CLEAN_FILES}")

if(OFFLINE)
  set(DISABLE_RESOLVE "-Doffline=true")
else()
  set(DISABLE_RESOLVE "-Doffline=false")
endif()

add_dependencies(build-claw-solution omni-compiler generate-xmods)
add_custom_command(
  TARGET build-claw-solution
  COMMAND ${Ant_EXECUTABLE} -f ${ANT_SOLUTION}
  ${ANT_FLAGS}
  -Dclaw.properties.dir=${CX2T_PROPERTIES_DIR}
  COMMENT "Building CLAW solution"
)

set(RUN_TEST "${CMAKE_SOURCE_DIR}/scripts/run-test.sh")
set(RUN_ANT_CMD ${Ant_EXECUTABLE} -f ${ANT_SOLUTION} ${ANT_FLAGS} -Dclaw.properties.dir=${CX2T_PROPERTIES_DIR})

add_custom_target(run-driver-unit-tests)
add_dependencies(run-driver-unit-tests build-claw-solution)
add_custom_command(
  TARGET run-driver-unit-tests
  COMMAND bash "${RUN_TEST}" \"${RUN_ANT_CMD} run-driver-unit-tests\" "${CLAWFC_UT_REPORT}"
  COMMENT "Running CLAWFC unit-tests"
)

add_custom_target(build-driver-unit-tests)
add_dependencies(build-driver-unit-tests build-claw-solution)
add_custom_command(
  TARGET build-driver-unit-tests
  COMMAND ${RUN_ANT_CMD} build-driver-unit-tests
  COMMENT "Building CLAWFC unit-tests"
)

add_custom_target(run-driver-tests)
add_dependencies(run-driver-tests build-claw-solution)
add_custom_command(
  TARGET run-driver-tests
  COMMAND bash "${RUN_TEST}" \"${RUN_ANT_CMD} run-driver-tests\" "${CLAWFC_TESTS_REPORT}"
  COMMENT "Running CLAWFC tests"
)

add_custom_target(build-driver-tests)
add_dependencies(build-driver-tests build-claw-solution)
add_custom_command(
  TARGET build-driver-tests
  COMMAND ${RUN_ANT_CMD} build-driver-tests
  COMMENT "Building CLAWFC tests"
)

add_custom_target(run-cx2t-unit-tests)
add_dependencies(run-cx2t-unit-tests build-claw-solution)
add_custom_command(
  TARGET run-cx2t-unit-tests
  COMMAND ${RUN_ANT_CMD} run-cx2t-unit-tests
  COMMENT "Running CX2T unit tests"
)

add_custom_target(build-cx2t-unit-tests)
add_dependencies(build-cx2t-unit-tests build-claw-solution)
add_custom_command(
  TARGET build-cx2t-unit-tests
  COMMAND ${RUN_ANT_CMD} build-cx2t-unit-tests
  COMMENT "Building CX2T unit-tests"
)

add_custom_target(run-claw-tests)
add_dependencies(run-claw-tests build-claw-solution)
add_custom_command(
  TARGET run-claw-tests
  COMMAND bash "${RUN_TEST}" \"${RUN_ANT_CMD} run-claw-tests\" "${CLAW_TESTS_REPORT}"
  COMMENT "Running CLAW tests"
)

add_custom_target(build-claw-tests)
add_dependencies(build-claw-tests build-claw-solution)
add_custom_command(
  TARGET build-claw-tests
  COMMAND ${RUN_ANT_CMD} build-claw-tests
  COMMENT "Building CLAW tests"
)

add_custom_target(run-tests)
add_dependencies(run-tests run-cx2t-unit-tests run-driver-unit-tests run-driver-tests run-claw-tests)

add_custom_target(build-tests)
add_dependencies(build-tests build-cx2t-unit-tests build-driver-unit-tests build-driver-tests build-claw-tests)

option(GENERATE_ECLIPSE_IDE_PROJECTS "Generate Eclipse IDE projects" ON)
if(GENERATE_ECLIPSE_IDE_PROJECTS)
  message(STATUS "Generating Eclipse projects")
  foreach(PROJECT_NAME cx2t cx2t-unit-tests clawfc clawfc-unit-tests clawfc-tests claw-tests)
      file(MAKE_DIRECTORY ${CMAKE_BINARY_DIR}/ide/eclipse/${PROJECT_NAME})
      configure_file(${CMAKE_SOURCE_DIR}/resources/eclipse/${PROJECT_NAME}.project.in
                     ${CMAKE_BINARY_DIR}/ide/eclipse/${PROJECT_NAME}/.project
                     @ONLY)
      configure_file(${CMAKE_SOURCE_DIR}/resources/eclipse/${PROJECT_NAME}.classpath.in
                     ${CMAKE_BINARY_DIR}/ide/eclipse/${PROJECT_NAME}/.classpath
                     @ONLY)
      configure_file(${CMAKE_SOURCE_DIR}/resources/eclipse/${PROJECT_NAME}.ant.builder.in
                     ${CMAKE_BINARY_DIR}/ide/eclipse/${PROJECT_NAME}/.externalToolBuilders/ant.launch
                     @ONLY)
  endforeach()
endif()
