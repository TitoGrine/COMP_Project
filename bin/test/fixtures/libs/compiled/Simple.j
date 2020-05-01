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

	iconst_1
	istore 5

	iload 5

	iconst_0
	ifeq
	istore 6

	bipush 30
	istore 1

	bipush 10
	istore 2

	iload 7

	iload 1

	bipush 10
	iadd
	istore 7

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 3

	aload 3
	iload 1
	iload 2
	invokevirtual Simple/add(II)I
	istore 4

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
