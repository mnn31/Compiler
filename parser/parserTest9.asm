	# /**
	#  * @author Manan Gupta
	#  * @version 2026-05-02
	#  */
	.data
varx:
	.word 0
vary:
	.word 0
varcount:
	.word 0
newline:
	.asciiz "\n"
	.text
	.globl main
main:
	li $v0 2
	la $t0 varx
	sw $v0 ($t0)	# store $v0 into varx
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 1
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	addu $v0 $t0 $v0
	la $t0 vary
	sw $v0 ($t0)	# store $v0 into vary
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	la $t0 vary
	lw $v0 ($t0)	# load vary into $v0
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	addu $v0 $t0 $v0
	la $t0 varx
	sw $v0 ($t0)	# store $v0 into varx
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	la $t0 vary
	lw $v0 ($t0)	# load vary into $v0
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	mult $t0 $v0
	mflo $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	la $t0 vary
	lw $v0 ($t0)	# load vary into $v0
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	ble $t0 $v0 endif1
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
	la $t0 vary
	lw $v0 ($t0)	# load vary into $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
endif1:
	li $v0 14
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 14
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	bne $t0 $v0 endif2
	li $v0 14
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 14
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	beq $t0 $v0 endif3
	li $v0 3
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
endif3:
	li $v0 14
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 14
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	bgt $t0 $v0 endif4
	li $v0 4
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
endif4:
endif2:
	li $v0 15
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 14
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	ble $t0 $v0 endif5
	li $v0 5
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
endif5:
	li $v0 1
	la $t0 varcount
	sw $v0 ($t0)	# store $v0 into varcount
while6:
	la $t0 varcount
	lw $v0 ($t0)	# load varcount into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 15
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	bgt $t0 $v0 endwhile6
	la $t0 varcount
	lw $v0 ($t0)	# load varcount into $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
	la $t0 varcount
	lw $v0 ($t0)	# load varcount into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 1
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	addu $v0 $t0 $v0
	la $t0 varcount
	sw $v0 ($t0)	# store $v0 into varcount
	j while6
endwhile6:
	li $v0 10	# normal termination
	syscall
