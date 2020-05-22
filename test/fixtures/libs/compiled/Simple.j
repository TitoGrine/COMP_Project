.class public Simple
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 5

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 4

	bipush 9
	istore_1

	bipush 10
	istore_2

	aload 4
	iload_1
	iload_2
	invokevirtual Simple/whatsNinePlusTen(II)I
	istore_3

	iload_3
	invokestatic io/println(I)V
	return
.end method

.method public whatsNinePlusTen(II)I
	.limit stack 3
	.limit locals 3

	aload_0
	bipush 10
	bipush 11
	invokevirtual Simple/add(II)I
	ireturn
.end method

.method public add(II)I
	.limit stack 2
	.limit locals 3

	iload_1
	iload_2
	iadd
	ireturn
.end method