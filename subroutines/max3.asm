#
# Reads three integers from the user and prints the greatest of the three.
#
# Computes the greatest by composing max2: max3(x, y, z) is just
# max2(max2(x, y), z). The same max2 helper from the previous exercise
# is included here so this file can be assembled on its own. main
# reads three values through acceptNum, sets up the arguments, and
# calls max3. max3 saves $ra and the third argument across the first
# max2 call, feeds that result and z into a second max2 call, then
# returns. Works with negative numbers because max2 uses a signed
# bgt. Corresponds to the Subroutines Lab max3 exercise.
#
# @author Manan Gupta
# @version 05/08/2026
#

.data
prompt1: .asciiz "Enter the first number: "
prompt2: .asciiz "Enter the second number: "
prompt3: .asciiz "Enter the third number: "
display: .asciiz "The greatest value is: "
newline: .asciiz "\n"
.text
main:
	la $a0, prompt1
	jal acceptNum
	move $t0, $v0        # x

	la $a0, prompt2
	jal acceptNum
	move $t1, $v0        # y

	la $a0, prompt3
	jal acceptNum
	move $t2, $v0        # z

	move $a0, $t0        # set up arguments for max3
	move $a1, $t1
	move $a2, $t2

	subu $sp, $sp, 4     # save $ra around max3 call
	sw $ra, ($sp)
	jal max3
	lw $ra, ($sp)
	addu $sp, $sp, 4

	move $t3, $v0        # save returned greatest value

	li $v0, 4            # print labeling message
	la $a0, display
	syscall

	li $v0, 1            # print the result
	move $a0, $t3
	syscall

	li $v0, 4            # print trailing newline
	la $a0, newline
	syscall

	li $v0, 10           # syscall 10 = exit
	syscall

# acceptNum: prints the prompt string in $a0, reads an integer, returns it in $v0.
acceptNum:
	li $v0, 4
	syscall
	li $v0, 5
	syscall
	jr $ra

# max3: returns the greatest of $a0, $a1, $a2 in $v0 via two max2 calls.
max3:
	subu $sp, $sp, 4     # save $ra so the inner max2 calls don't lose it
	sw $ra, ($sp)
	subu $sp, $sp, 4     # also stash z across the first max2 call
	sw $a2, ($sp)

	jal max2             # $v0 = max2(x, y)

	lw $a2, ($sp)        # restore z
	addu $sp, $sp, 4

	move $a0, $v0        # max2(max2(x, y), z)
	move $a1, $a2
	jal max2

	lw $ra, ($sp)
	addu $sp, $sp, 4
	jr $ra

# max2: returns the greater of $a0 and $a1 in $v0 using a signed compare.
max2:
	bgt $a0, $a1, max2First
	move $v0, $a1
	jr $ra
max2First:
	move $v0, $a0
	jr $ra
