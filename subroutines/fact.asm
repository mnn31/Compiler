#
# Reads a non-negative integer n from the user and prints n!.
#
# The fact subroutine implements n! recursively, mirroring the
# Java method fact(n) = n * fact(n - 1) with base case fact(0) = 1.
# Each recursive level pushes $ra and the current $a0 onto the
# stack, recurses on n - 1, then pops both and multiplies the
# returned subproblem value by n via mult / mflo. main reads n
# with syscall 5, calls fact, and prints the answer. Corresponds
# to the Subroutines Lab fact exercise.
#
# @author Manan Gupta
# @version 05/08/2026
#

.data
prompt: .asciiz "Enter a non-negative integer: "
display: .asciiz "The factorial is: "
newline: .asciiz "\n"
.text
main:
	li $v0, 4            # print prompt
	la $a0, prompt
	syscall

	li $v0, 5            # read n
	syscall
	move $a0, $v0        # n -> $a0 (argument for fact)

	subu $sp, $sp, 4     # save $ra around fact call
	sw $ra, ($sp)
	jal fact
	lw $ra, ($sp)
	addu $sp, $sp, 4

	move $t0, $v0        # save result

	li $v0, 4            # print labeling message
	la $a0, display
	syscall

	li $v0, 1            # print the result
	move $a0, $t0
	syscall

	li $v0, 4            # print trailing newline
	la $a0, newline
	syscall

	li $v0, 10           # syscall 10 = exit
	syscall

# fact: recursive n!. Argument in $a0, result in $v0.
fact:
	bnez $a0, factRecurse # if n != 0 recurse
	li $v0, 1             # base case: 0! = 1
	jr $ra
factRecurse:
	subu $sp, $sp, 4      # push $ra
	sw $ra, ($sp)
	subu $sp, $sp, 4      # push current n so it survives the recursive call
	sw $a0, ($sp)

	subu $a0, $a0, 1      # recurse on n - 1
	jal fact

	lw $a0, ($sp)         # restore n
	addu $sp, $sp, 4

	mult $a0, $v0         # n * fact(n - 1)
	mflo $v0

	lw $ra, ($sp)         # pop $ra
	addu $sp, $sp, 4
	jr $ra
