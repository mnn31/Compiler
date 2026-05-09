#
# Reads two integers from the user and prints whichever value is greater.
#
# Promps the user for two values through a small acceptNum helper that
# prints the prompt in $a0 with syscall 4 and reads an integer with
# syscall 5. Loads the two inputs into $a0 and $a1 and calls the max2
# subroutine, which compares them with bgt and returns the larger
# value in $v0. main prints a labeling string and the result, then
# exits cleanly via syscall 10. Handles negative numbers because bgt
# performs a signed comparison. Corresponds to the Subroutines Lab
# max2 exercise.
#
# @author Manan Gupta
# @version 05/08/2026
#

.data
prompt1: .asciiz "Enter the first number: "
prompt2: .asciiz "Enter the second number: "
display: .asciiz "The greater value is: "
newline: .asciiz "\n"
.text
main:
	la $a0, prompt1      # prompt for first input
	jal acceptNum
	move $t0, $v0        # $t0 = first input

	la $a0, prompt2      # prompt for second input
	jal acceptNum
	move $t1, $v0        # $t1 = second input

	move $a0, $t0        # set up arguments for max2
	move $a1, $t1
	jal max2             # $v0 = max2($a0, $a1)
	move $t2, $v0        # save returned greater value

	li $v0, 4            # print labeling message
	la $a0, display
	syscall

	li $v0, 1            # print the result
	move $a0, $t2
	syscall

	li $v0, 4            # print trailing newline
	la $a0, newline
	syscall

	li $v0, 10           # syscall 10 = exit
	syscall

# acceptNum: prints the prompt string in $a0, reads an integer, returns it in $v0.
acceptNum:
	li $v0, 4            # syscall 4 = print string ($a0 already holds the address)
	syscall
	li $v0, 5            # syscall 5 = read integer
	syscall
	jr $ra

# max2: returns the greater of $a0 and $a1 in $v0 using a signed compare.
max2:
	bgt $a0, $a1, max2First
	move $v0, $a1        # second is greater (or equal)
	jr $ra
max2First:
	move $v0, $a0        # first is strictly greater
	jr $ra
