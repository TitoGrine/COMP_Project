.class public Simple
.super java/lang/Object

.field public jk I 
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

	iload 1
	
	invokevirtual Simple/add(IZ)I
	istore 6

	iload 6
	invokestatic io/println(I)V
	return
.end method


.method public add(IZ)I
	.limit stack 99
	.limit locals 99

	iload 1

	bipush 3
	imul
	bipush 3
	iadd

	aload_0
	bipush 1
	iload 2
	
	invokevirtual Simple/cenas(IZ)I
	iadd


	ireturn
.end method

.method public cenas(IZ)I
	.limit stack 99
	.limit locals 99

	bipush 3

	ireturn
.end method