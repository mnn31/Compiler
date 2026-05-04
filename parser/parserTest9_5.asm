	# /**
	#  * @author Manan Gupta
	#  * @version 2026-05-02
	#  */
	.data
varx:
	.word 0
vary:
	.word 0
varsum:
	.word 0
vari:
	.word 0
varb:
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
	li $v0 3
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	mult $t0 $v0
	mflo $v0
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
	li $v0 0
	la $t0 varx
	sw $v0 ($t0)	# store $v0 into varx
while2:
	la $t0 varx
	lw $v0 ($t0)	# load varx into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 8
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	bge $t0 $v0 endwhile2
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
	addu $v0 $t0 $v0
	la $t0 varx
	sw $v0 ($t0)	# store $v0 into varx
	j while2
endwhile2:
	li $v0 0
	la $t0 varsum
	sw $v0 ($t0)	# store $v0 into varsum
	li $v0 1
	la $t0 vari
	sw $v0 ($t0)	# init i for FOR loop
for3:
	la $t0 vari
	lw $v0 ($t0)
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 5
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	bgt $t0 $v0 endfor3
	la $t0 varsum
	lw $v0 ($t0)	# load varsum into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	la $t0 vari
	lw $v0 ($t0)	# load vari into $v0
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	addu $v0 $t0 $v0
	la $t0 varsum
	sw $v0 ($t0)	# store $v0 into varsum
	la $t0 varsum
	lw $v0 ($t0)	# load varsum into $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
	la $t0 vari
	lw $v0 ($t0)
	addu $v0 $v0 1	# bump i
	sw $v0 ($t0)
	j for3
endfor3:
	li $v0 5	# READLN -- read int syscall
	syscall
	la $t0 varb
	sw $v0 ($t0)	# store input into varb
	la $t0 varb
	lw $v0 ($t0)	# load varb into $v0
	subu $sp $sp 4
	sw $v0 ($sp)	# push $v0
	li $v0 10
	lw $t0 ($sp)	# pop into $t0
	addu $sp $sp 4
	bge $t0 $v0 endif4
	la $t0 varb
	lw $v0 ($t0)	# load varb into $v0
	move $a0 $v0	# print int in $v0
	li $v0 1
	syscall
	la $a0 newline	# print newline
	li $v0 4
	syscall
endif4:
	li $v0 10	# normal termination
	syscall
