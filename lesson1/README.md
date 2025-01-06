# JavaConcurrencyTutorial

## Lesson 1: Threads

### Prelude

Throughout this tutorial, we'll discuss various so-called *concurrency primitives*, which are
different ways to achieve *concurrency* in computing. Before we begin, let's define exactly what's
meant by concurrency. Simply put, concurrency is a mode of computing that allows you to run
multiple tasks at once, where a *task* is merely a sequence of instructions. Even if you don't have
any experience with concurrency, you've probably already used it at least one form.

The most basic form of concurrency is multiprocessing, which means running multiple processes at
once. When, for instance, you run a web browser and a code editor on your computer at the same time,
your computer is performing multiprocessing—executing instantiations of multiple programs and
managing the resources (e.g. compute, memory, storage, etc.) they share. Multiprocessing wouldn't be
that useful if it weren't used to achieve *multitasking*, which is the more abstract notion of
running multiple tasks at the same time.

Multiprocessing isn't only the only means of multitasking. In fact, it's far from the most
effective. Having to run a separate process for each task to be executed concurrently would be
disastrous because even something as simple as browsing a website necessitates running
multiple tasks at once. When you navigate to your bank's website, hundreds of tasks are happening
at once: assets are fetched, requests for information to servers are being made, elements are
rendered to the page, and processing is being done to coordinate all of these tasks and respond to
your input. Having to execute each of those tasks in a separate process would impose far too much
overhead. It would also be confusing from the perspective of an operating system user. Imagine if,
when you opened your operating system's system monitor/task manager, you saw thousands of processes
belonging to Firefox.

Threads are another, more lightweight means of multitasking that we'll delve into in this section.

### Introduction to Threads

A *thread* is one step up from a process in abstraction. Threads are spawned by processes and run
independently from each other, and a process can usually spawn as many threads as it pleases
(although most operating systems impose very high limits). You can think of a thread as a subprocess
that's owned by the process that spawns it. The main difference between spawning a thread and
another process is that threads can share memory, making communication between them far easier than
inter-process communication. Another difference is that when a process is killed, all of its
threads are as well.

A good analogy for the relationship between threads and processes is the relationship between a
restaurant worker and the restaurant they work in. Restaurants operate independently from each other
and don't communicate among each other much. They compete for resources (i.e. business with
customers), and they generally work towards different goals. Restaurant workers also operate
independently, but they can share resources (e.g. tables, the kitchen, ingredients, etc.) and a
great deal of communication occurs between them because they're all working towards a shared goal.

### Example

Let's consider the following Java program:

```java
public class Main {
	public static void main(String[] arguments) {
		for (int i = 0; i < 20; i++) {
			System.out.println(i);
		}
	}
}
```

When this program is run, the numbers between 0 and 20 exclusive will be printed by a
single process. A common use case for threads is dividing up work so that it's run *asynchronously*
(out of order) instead of *synchronously* (in a linear order), so let's divide up the work of
printing these numbers by spawning some threads:

```java
// Example1.java

public class CountingThread extends Thread {
	private final int start;
	private final int end;

	public CountingThread(int start, int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public void run() {
		try {
			for (int i = start; i < end; i++) {
				System.out.println(i);

				Thread.sleep(500);
			}
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		}
	}
}

public class Main {
	public static void main(String[] arguments) {
		CountingThread thread1 = new CountingThread(0, 5);
		CountingThread thread2 = new CountingThread(5, 10);

		thread1.start();
		thread2.start();
	}
}
```

Now, let's run this program:

```
$ java Example1.java
5
0
6
1
7
2
8
3
9
4
```

So, what happened? We defined a thread called `CountingThread` by extending the `Thread` class:

```java
public class CountingThread extends Thread {
```

This thread accepts two arguments, `start` and `end`:

```java
public CountingThread(int start, int end) {
	this.start = start;
	this.end = end;
}
```

When run, it prints the numbers between `start` and `end`:

```java
@Override
public void run() {
	try {
		for (int i = start; i < end; i++) {
			System.out.println(i);

			Thread.sleep(500);
		}
	} catch (InterruptedException exception) {
		exception.printStackTrace();
	}
}
```

Don't mind the `Thread.sleep` call. We've included that only to slow down the printing and
demonstrate that the threads can print numbers at the same time. We've included the
`try`/`catch` statement because `Thread.sleep` may throw `InterruptedException`, which the compiler
forces us to catch.

Then we constructed two instances of the thread and spawned those instances:

```java
CountingThread thread1 = new CountingThread(0, 5);
CountingThread thread2 = new CountingThread(5, 10);

thread1.start();
thread2.start();
```

### Order of Execution

When our program ran, it printed the numbers out of order. This is because the order in which the
threads run is *undefined behavior*. The operating system doesn't guarantee the order in which it
chooses to *schedule* the threads because they're executed asynchronously—completely independently
of one another. It could choose to let `thread1` run to completion and then `thread2`, vice versa,
or to interleave them in some way. In this case, the operating system chose to interleave them
perfectly, running each thread for a single number and *preempting* (i.e. pausing) it when it chose
to sleep.

We'll explain why this happened in a later lesson. For now, just keep in mind that at any point,
your thread may be paused by the operating system and resumed at an undefined point in the future.
This means that no two lines of code are guaranteed to be executed in order. Forgetting this may
lead to *race conditions*, in which code is written with the false expectation that a set of
operations will be executed together.

### The Main Thread

It's important to note that when this program runs, there are actually 3 running threads, not 2.
Every process begins with a so-called main thread, and additional threads are spawned by this
main thread or an already-spawned thread. Even our synchronous program has a main thread:

```java
public class Main {
	public static void main(String[] arguments) {
		for (int i = 0; i < 20; i++) {
			System.out.println(i);
		}
	}
}
```

Hence, this program is referred to as *single-threaded*. Our second program is referred to as
*multithreaded*.

### Thread Joining

Something we haven't yet addressed is that after the two threads have spawned and while they're
doing work, the main thread is doing nothing. In other words, it's *idle*. Normally, when a thread
is idle and every one of its instructions have been executed, it's terminated. For example, in
this program, the main thread is idle and has exhausted its instructions; hence, the program
immediately terminates:

```java
public class Main {
	public static void main(String[] arguments) {}
}
```

Why did the program continue running in our earlier example, even after the main thread had
finished? The answer is that a program doesn't terminate until all of its threads have terminated.
So it isn't until `thread1` and `thread2` have both completed that the program terminates.

If we want to tell the main thread to wait until `thread1` and `thread2` have terminated before
terminating itself, we can call `Thread#join`. Here, the main thread (that executing the `main`
method) won't terminate until `thread1` and `thread2` do:

```java
// Example2.java

...

public class Main {
	public static void main(String[] arguments) throws InterruptedException {
		CountingThread thread1 = new CountingThread(0, 5);
		CountingThread thread2 = new CountingThread(5, 10);

		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();

		System.out.println("Finished!")
	}
}
```

Hence, "Finished!" isn't printed until we've printed all the numbers:

```
$ java Example2.java
0
5
1
6
2
7
3
8
4
9
Finished!
```

If we omit `thread1.join()` and `thread2.join()`, "Finished!" may be printed before both threads
complete.

```java
// Example3.java

...

public class Main {
	public static void main(String[] arguments) {
		CountingThread thread1 = new CountingThread(0, 5);
		CountingThread thread2 = new CountingThread(5, 10);

		thread1.start();
		thread2.start();

		System.out.println("Finished!")
	}
}
```

```
$ java Example3.java
0
Finished!
5
1
6
2
7
3
8
4
9
```

### Sharing Memory Between Threads

As mentioned previously, threads, unlike processes, can share memory. That means that `Thread`
subclasses can define methods that return data owned by the threads to which those classes belong,
and those methods can be called from other threads (e.g. the main thread). Consider the
following example:

```java
// Example4.java

public class SumThread extends Thread {
	private final int start;
	private final int end;

	public int result = 0;

	public SumThread(int start, int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public void run() {
		int currentSum = 0;

		for (int i = start; i < end; i++) {
			currentSum += i;
		}

		result = currentSum;
	}
}

public class Main {
	public static void main(String[] arguments) throws InterruptedException {
		SumThread thread1 = new SumThread(1, 500);
		SumThread thread2 = new SumThread(500, 1000);

		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();

		System.out.printf("1 + ... + 1,000 = %d\n", thread1.result + thread2.result);
	}
}
```

```
$ java Example4.java
1 + ... + 1,000 = 499500
```

Here, `thread1.result` is owned by `thread1` and `thread2.result` is owned by `thread2`. However, at
any point, the main thread can access them and get the current value. This is because threads share
memory. If, instead, we ran them as separate programs instead of separate threads, they wouldn't be
able to communicate so directly.

### Homework

After having read this lesson, you should have a basic understanding of threads and how to use them.
To put your knowledge into practice, consider completing this lesson's homework task, located in
[Main.java](./src/main/java/com/github/jadenpete/javaconcurrencytutorial/lesson1/Main.java).
