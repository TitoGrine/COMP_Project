.class public Simple
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 6
	.limit locals 9

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 8

	iconst_0
	istore 5

	iconst_4
	istore_1

	iconst_5
	istore_2

	bipush 6
	istore_3

	bipush 7
	istore 4

	iconst_1
	istore 6

	iconst_1
	istore 7

	iload 6
	iload 7
	iand
	ifeq else_0
		aload 8
		iload_1
		iload_2
		iload_3
		iload 4
		invokevirtual Simple/add(IIII)I
		istore 5

		goto endif_0
	else_0:
	endif_0:

	iload 5
	invokestatic io/println(I)V
	return
.end method

.method public add(IIII)I
	.limit stack 2
	.limit locals 5

	iload_1
	iload_2
	iadd
	iload_3
	iadd
	iload 4
	iadd
	ireturn
.end method