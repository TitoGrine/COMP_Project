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
	istore 1

	iload 1

	iconst_0
	iand
	bipush 1
	bipush 3
	if_icmple l1
	iconst_0
	goto l2
l1:
	iconst_1
l2:
	iand
	istore 5

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
	istore 6

	iconst_1
	istore 1

	iload 6
	invokestatic io/println(I)V
	iload 6
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
