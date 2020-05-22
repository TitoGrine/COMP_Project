.class public Simple
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 9

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore_1

	iconst_3
	istore_2

	iconst_5
	istore_3

	bipush 8
	istore 4

	bipush 9
	istore 5

	iconst_1
	istore 6

	iconst_1
	istore 7

	iload_3
	iload_2
	if_icmplt branch_0
		iconst_0
		goto end_0
	branch_0:
		iconst_1
	end_0:
	ineg
	iload 6
	iload 7
	iand
	iand
	ifeq else_0
		iconst_1
		istore 8

		goto endif_0
	else_0:
		iconst_1
		istore 8

	endif_0:

	iload 8
	invokestatic io/println(I)V
	return
.end method