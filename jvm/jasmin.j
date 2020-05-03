.public class Simple
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
	istore 1

	bipush 0
	bipush 10
	isub
	istore 2

	new Simple
	dup
	invokespecial Simple/<init>()V
	astore 3

	iload 1
	iload 2
	invokevirtual Simple/add(II)I
	istore 4

	iload 4
	invokestatic Simple/println(I)V
	return
.end method


.method public add(II)I
	.limit stack 99
	.limit locals 99

	iload 1

	iload 2

	bipush 8
	imul
	bipush 1
	bipush 2
	iadd
	idiv
	iadd

	ireturn
.end method
