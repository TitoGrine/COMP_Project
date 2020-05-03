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

	aload_0
	bipush 10
	istore 2

	aload_0
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

	aload_0
	bipush 3
	putfield Simple/jk I
	getfield  Simple/jk I


	ireturn
.end method

.method public cenas(I)I
	.limit stack 99
	.limit locals 99

	aload_0
	bipush 10
	istore 2

	iload 1

	iload 2
	iadd


	ireturn
.end method
