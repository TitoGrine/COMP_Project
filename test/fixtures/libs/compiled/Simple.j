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

	bipush 1
	bipush 3
	if_icmple l1
	iconst_0
	goto l2
l1:
	iconst_1
l2:
	istore 1

	bipush 30
	istore 2

	iload 3

	bipush 0
	bipush 10
	isub
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

	iload 5
	invokestatic io/println(I)V
	return
.end method


.method public add(II)I
	.limit stack 99
	.limit locals 99

	iload 1

	iload 2
	iadd

	ireturn
.end method
