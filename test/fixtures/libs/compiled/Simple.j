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

	bipush 3
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

	bipush 3
	istore 3

	iload 3

	ireturn
.end method
