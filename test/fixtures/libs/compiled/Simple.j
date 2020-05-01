.class public Simple
.super java/lang/Object

.method <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 99
	.limit locals 99

<<<<<<< HEAD
=======
	iconst_1
	istore 5

	iload 5

	iconst_0
	ifeq
	istore 6

>>>>>>> ca17a42b8b5702a2f5db81413ca927b58fb4212e
	bipush 30
	istore 2

	bipush 10
	istore 3

	iload 7

	iload 1

	bipush 10
	iadd
	istore 7

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 4

	aload 4
	iload 2
	iload 3
	invokevirtual Simple/add(II)I
	istore 5

	iload 5
	invokestatic io/println(I)V
	return
.end method


.method public add(II)I
	.limit stack 99
	.limit locals 99

	iload 1

	iand 2
	iadd

	ireturn
.end method
