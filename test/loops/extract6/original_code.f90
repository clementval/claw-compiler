!
! This file is released under terms of BSD license
! See LICENSE file for more information
!
! Simple program to test the loop-extract directive with fusion option
!
module mod

  contains

subroutine LOOP_EXTRACT
  INTEGER :: value1(10)
  INTEGER :: value2(10)
  INTEGER :: j
  INTEGER :: istart=1
  INTEGER :: iend=10
  DO j=1,10
    value1(j) = j
    value2(j) = j
  END DO




  !$claw loop-extract range(i=istart,iend) map(value1:i) map(value2:i) &
  !$claw fusion group(i)
  CALL clawloop(value1, value2)

  !$claw loop-fusion group(i)
  DO i = istart, iend
    PRINT *, 'dummy loop'
  END DO
END

SUBROUTINE clawloop(value1, value2)
  INTEGER :: value1(10)
  INTEGER :: value2(10)
  INTEGER :: i
  INTEGER :: istart=1
  INTEGER :: iend=10

  DO i=istart,iend
    print*,'value1: ',value1(i)
    print*,'value2: ',value2(i)
  END DO

END
end module mod

program main
  use mod

  call LOOP_EXTRACT()
end program main
