.class public Simple
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 6
	.limit locals 10

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

	aload_1
	iload_2
	iload_3
	invokevirtual Simple/add(II)I
	istore 6

	aload_1
	iload_2
	iload_3
	iload 4
	invokevirtual Simple/add(III)I
	istore 7

	aload_1
	iload_2
	iload_3
	iload 4
	iload 5
	invokevirtual Simple/add(IIII)I
	istore 8

	aload_1
	iload 6
	iload 7
	iload 8
	invokevirtual Simple/add(III)I
	istore 9

	iload 6
	invokestatic io/println(I)V
	iload 7
	invokestatic io/println(I)V
	iload 8
	invokestatic io/println(I)V
	iload 9
	invokestatic io/println(I)V
	return
.end method

.method public add(II)I
	.limit stack 2
	.limit locals 3

	iload_1
	iload_2
	iadd
	ireturn
.end method

.method public add(III)I
	.limit stack 2
	.limit locals 4

	iload_1
	iload_2
	iadd
	iload_3
	iadd
	ireturn
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