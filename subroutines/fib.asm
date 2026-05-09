#
# Reads a non-negative integer n from the user and prints fib(n).
#
# The fib subroutine implements the standard recurrence
# fib(n) = fib(n - 1) + fib(n - 2) with base case fib(n) = n
# for n <= 1. Each recursive level pushes $ra and the current n,
# recurses on n - 1, then pushes the returned fib(n - 1) so it
# survives the second recursive call on n - 2 and adds the two
# subproblem results. main reads n with syscall 5, invokes fib,
# and prints the answer. Corresponds to the Subroutines Lab fib
# exercise.
#
# @author Manan Gupta
# @version 05/08/2026
#

.data
prompt: .asciiz "Enter a non-negative integer: "
display: .asciiz "Fibonacci(n) = "
newline: .asciiz "\n"
.text
main:
	li $v0, 4            # print prompt
	la $a0, prompt
	syscall

	li $v0, 5            # read n
	syscall
	move $a0, $v0        # n -> $a0

	subu $sp, $sp, 4     # save $ra around fib call
	sw $ra, ($sp)
	jal fib
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

# fib: recursive fib(n). Argument in $a0, result in $v0.
fib:
	bgt $a0, 1, fibRecurse # n > 1 -> recurse
	move $v0, $a0          # base case: fib(0) = 0, fib(1) = 1
	jr $ra
fibRecurse:
	subu $sp, $sp, 4       # push $ra
	sw $ra, ($sp)
	subu $sp, $sp, 4       # push n so it's available for the second call
	sw $a0, ($sp)

	subu $a0, $a0, 1       # fib(n - 1)
	jal fib

	lw $a0, ($sp)          # restore n
	addu $sp, $sp, 4

	subu $sp, $sp, 4       # push fib(n - 1) so it survives the second call
	sw $v0, ($sp)

	subu $a0, $a0, 2       # fib(n - 2)
	jal fib

	lw $t0, ($sp)          # pop fib(n - 1) into $t0
	addu $sp, $sp, 4

	addu $v0, $t0, $v0     # fib(n - 1) + fib(n - 2)

	lw $ra, ($sp)          # pop $ra
	addu $sp, $sp, 4
	jr $ra
