	# /**
	#  * @author Manan Gupta
	#  * @version 2026-05-02
	#  */
	.data
varx:
	.word 0
vary:
	.word 0
newline:
	.asciiz "\n"
	.text
	.globl main
main:
	li $v0 5	# READLN -- read int syscall
	syscall
	la $t0 varx
	sw $v0 ($t0)	# store input into varx
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	addu $v0 $t0 $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
	li $v0 1
	la $t0 vary
	sw $v0 ($t0)	# init y for FOR loop
for1:
	la $t0 vary
	lw $v0 ($t0)
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 12
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	bgt $t0 $v0 endfor1
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
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
	addu $v0 $t0 $v0
	la $t0 varx
	sw $v0 ($t0)	# store $v0 into varx
	la $t0 vary
	lw $v0 ($t0)
	addu $v0 $v0 1	# bump y
	sw $v0 ($t0)
	j for1
endfor1:
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
while2:
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 75
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	ble $t0 $v0 endwhile2
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
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
	li $v0 2
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	subu $v0 $t0 $v0
	la $t0 varx
	sw $v0 ($t0)	# store $v0 into varx
	j while2
endwhile2:
	li $v0 10	# normal termination
	syscall
