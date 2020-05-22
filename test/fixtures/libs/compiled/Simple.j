.class public Simple
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 6
	.limit locals 5

	iconst_2
	istore_1

	iconst_5
	istore_2

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 4

	aload 4
	iload_1
	iload_2
	bipush 7
	bipush 9
	invokevirtual Simple/spam_calculator(IIII)I
	istore_3

	iload_3
	invokestatic io/println(I)V
	return
.end method

.method public spam_calculator(IIII)I
	.limit stack 6
	.limit locals 7

	ldc 1534
	istore 5

	ldc 191
	istore 6

	iconst_2
	iload 6
	imul
	iload 6
	iload_1
	iadd
	iconst_5
	iconst_3
	imul
	isub
	iadd
	iload 5
	isub
	bipush 34
	isub
	bipush 26
	iload 6
	imul
	ldc 2345
	iadd
	iload_2
	iconst_3
	iload_3
	iadd
	iconst_2
	bipush 9
	imul
	isub
	imul
	isub
	iadd
	ireturn
.end method