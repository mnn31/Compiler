#
# Builds a small linked list on the heap and prints the sum of its values.
#
# newlistnode allocates 8 bytes via syscall 9 (sbrk), stores the
# value argument in the first word and the next pointer in the
# second, and returns the node address in $v0. Address 0 is used
# to represent null. sumlist walks the list recursively: if the
# pointer is 0 it returns 0; otherwise it loads the value, recurses
# on the next pointer, and adds the two together. main constructs
# the list 10 -> 20 -> 30 -> null tail-first, calls sumlist, and
# prints 60. Corresponds to the Subroutines Lab sumlist /
# newlistnode exercise; the linked list lives on the heap.
#
# @author Manan Gupta
# @version 05/08/2026
#

.data
display: .asciiz "Sum of list: "
newline: .asciiz "\n"
.text
main:
	li $a0, 30           # tail node: value 30, next = null
	li $a1, 0
	subu $sp, $sp, 4
	sw $ra, ($sp)
	jal newlistnode
	lw $ra, ($sp)
	addu $sp, $sp, 4
	move $t0, $v0        # tail address

	li $a0, 20           # middle node: value 20, next = tail
	move $a1, $t0
	subu $sp, $sp, 4
	sw $ra, ($sp)
	jal newlistnode
	lw $ra, ($sp)
	addu $sp, $sp, 4
	move $t0, $v0

	li $a0, 10           # head node: value 10, next = middle
	move $a1, $t0
	subu $sp, $sp, 4
	sw $ra, ($sp)
	jal newlistnode
	lw $ra, ($sp)
	addu $sp, $sp, 4
	move $t0, $v0        # head address

	move $a0, $t0        # walk the list
	subu $sp, $sp, 4
	sw $ra, ($sp)
	jal sumlist
	lw $ra, ($sp)
	addu $sp, $sp, 4
	move $t1, $v0        # sum

	li $v0, 4            # print labeling message
	la $a0, display
	syscall

	li $v0, 1            # print the sum
	move $a0, $t1
	syscall

	li $v0, 4            # print trailing newline
	la $a0, newline
	syscall

	li $v0, 10           # syscall 10 = exit
	syscall

# newlistnode: allocates 8 bytes on the heap, stores value ($a0) in the
# first word and next ($a1) in the second word. Returns address in $v0.
newlistnode:
	move $t0, $a0        # save value before sbrk overwrites $a0
	move $t1, $a1        # save next
	li $a0, 8            # syscall 9 = sbrk; $a0 = bytes to allocate
	li $v0, 9
	syscall              # $v0 = address of the new 8-byte block
	sw $t0, 0($v0)       # value -> first word
	sw $t1, 4($v0)       # next  -> second word
	jr $ra

# sumlist: recursively sums the values of the list whose head is in $a0.
# Address 0 represents null. Returns the sum in $v0.
sumlist:
	bnez $a0, sumlistRecurse # list != null -> recurse
	li $v0, 0                # base case: null -> 0
	jr $ra
sumlistRecurse:
	subu $sp, $sp, 4         # push $ra
	sw $ra, ($sp)
	lw $t0, 0($a0)           # load this node's value
	subu $sp, $sp, 4         # push it so it survives the recursive call
	sw $t0, ($sp)

	lw $a0, 4($a0)           # recurse on next pointer
	jal sumlist

	lw $t0, ($sp)            # pop saved value
	addu $sp, $sp, 4

	addu $v0, $v0, $t0       # value + sumlist(next)

	lw $ra, ($sp)            # pop $ra
	addu $sp, $sp, 4
	jr $ra
