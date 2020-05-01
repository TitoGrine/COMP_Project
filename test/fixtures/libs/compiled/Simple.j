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

	bipush 30
	istore 2

	bipush 10
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

	aload 4
	iload 5
	iload 3
	invokevirtual Simple/cenas(II)I
	istore 6

	iload 6
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

.method public cenas(II)I
	.limit stack 99
	.limit locals 99

	iload 1

	iload 2
	iadd

	ireturn
.end method
