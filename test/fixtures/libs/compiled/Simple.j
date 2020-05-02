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

	bipush 10
	istore 2

	bipush 2
	istore 3

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 4

	aload 4
	iload 2
	iload 3
	invokevirtual Simple/add(II)I
	istore 5

	iconst_1
	istore 1

	iload 5
	invokestatic io/println(I)V
	return
.end method


.method public add(II)I
	.limit stack 99
	.limit locals 99

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 3

	aload 3
	iload 1
	invokevirtual Simple/cenas(I)I

	ireturn
.end method

.method public cenas(I)I
	.limit stack 99
	.limit locals 99

	bipush 10
	istore 2

	iload 2

	iload 1
	iadd

	ireturn
.end method
