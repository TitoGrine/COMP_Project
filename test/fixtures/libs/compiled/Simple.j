.class public Simple
.super java/lang/Object


.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 6

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 4

	aload 4
	invokevirtual Simple/instance()LSimple;
	astore 5

	bipush 20
	istore_1

	bipush 20
	istore_2

	aload 5
	iload_1
	iload_2
	invokevirtual Simple/add(II)I
	invokestatic io/println(I)V
	return
.end method

.method public instance()LSimple;
	.limit stack 2
	.limit locals 1

	new Simple
	dup
	invokespecial Simple/<init>()V
	areturn
.end method

.method public add(II)I
	.limit stack 2
	.limit locals 3

	iload_1
	iload_2
	iadd
	ireturn
.end method