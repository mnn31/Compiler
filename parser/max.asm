	# /**
	#  * @author Manan Gupta
	#  * @version 2026-05-02
	#  */
	.data
vara:
	.word 0
varb:
	.word 0
varm:
	.word 0
newline:
	.asciiz "\n"
	.text
	.globl main
main:
	li $v0 10
	la $t0 vara
	sw $v0 ($t0)	# store $v0 into vara
	li $v0 20
	la $t0 varb
	sw $v0 ($t0)	# store $v0 into varb
	la $t0 vara
	lw $v0 ($t0)	# load vara into $v0
	la $t0 varm
	sw $v0 ($t0)	# store $v0 into varm
	la $t0 varb
	lw $v0 ($t0)	# load varb into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	la $t0 varm
	lw $v0 ($t0)	# load varm into $v0
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	ble $t0 $v0 endif1
	la $t0 varb
	lw $v0 ($t0)	# load varb into $v0
	la $t0 varm
	sw $v0 ($t0)	# store $v0 into varm
endif1:
	la $t0 varm
	lw $v0 ($t0)	# load varm into $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
	li $v0 10	# normal termination
	syscall
