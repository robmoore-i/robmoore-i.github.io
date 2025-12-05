# Thoughts on Spring dependency injection

This page attempts to distill something about Spring's dependency injection framework that has always put me off it: That it superficially improves the experience of code authors, at the expense of code readers.

I could make an outrageous straw man argument as I'm sure many others have already done, but actually I don't intend to paint a picture that anything is obviously wrong with Spring's dependency injection. It's a choice, it's a tradeoff. The framework works well, it's popular, and Netflix uses it!

The problem I have is that Spring's dependency injection framework makes is impossible to step through the program statically. This feature is what causes me to usually have a bad [reader experience](/writing/software_development/reader-experience) when looking at Spring programs, even though I know that _writing_ the program feels easy and unproblematic.

## Author experience in Spring

When you're writing code in a Spring app, you can add new behaviour to the program by writing a new class with the right annotation and making it implement some well-documented interface. You don't need to declare this class anywhere else, it automatically becomes part of the program's behaviour, because a Spring Application scans the classpath in order to find all the relevant objects for use in the program.

If you are among the enlightened minority that likes to write automatic tests which prove that your program does what you think it does, then you'll like the fact that Spring provides convenient mechanisms for replacing objects in the dependency injection graph with appropriate test doubles you define. This also works by classpath scanning, so again you can just add a class with the right annotation, and it will replace whatever it is expected to replace, assuming you've done everything right.

When you write a Spring Bean, you don't need to think about how it fits into the rest of the program, because that is handled by the framework. The code you've written ends up in the right place at the right time, and you, the author, don't need to worry about that as long as it makes the tests pass. 

**The author experience is mostly easy, _as long as you aren't thinking about how it works_.**

## Reader experience in Spring

When you're reading a Spring app, there isn't an obvious entrypoint from which you can navigate through the rest of the program. This is because the behaviour of the program is determined at runtime by scanning the classpath. Behaviour can be added or modified by writing a new class that neither references any other class, nor is referenced by one. It can also be added or modified by runtime dependencies of the program i.e. classes added to the classpath at runtime.

To understand what a Spring program does by reading the code, you need to go through all the classes that might add _Spring Beans_ to the classpath. There is no concept of navigating from the root of the program "down" through the program's call tree. Disconnected islands of code define the program, so to understand it, you must visit (i.e. read) each of them, and understand how the framework glues them together.

**The surface-level simplicity of the author experience trades off against a challenging and opaque reader experience.**

## What if you're already a Spring expert?

If you're an expert in Spring, you don't need to read the structural glue because you already know what's happening under the hood and which kinds of Spring Beans might be influencing the behaviour of the program from afar. Additionally, if you're adept with an interactive debugger, you can step through the program at runtime, rather than by reading it 'offline', although that is obviously a terrible experience compared to reading the code, as you can do if you instead pass arguments to functions for dependency injection, or even use something like [Guice](https://github.com/google/guice).

**It isn't an interesting observation to note that experts rarely struggle in their area of expertise.**

## Concluding thoughts

When discussing code, I prefer to talk about ["reader experience"](/writing/software_development/reader-experience) rather than "readability", because it's important to consider the audience. If you have a team of truly expert Spring developers, they are probably going to have a perfectly good reader experience in a Spring program.

A developer who merely has any experience in Spring development is probably not actually expert, and in my opinion, the vast majority of amateur "Spring developers" out there would probably have a much easier time reading and making sense of straightforward, non-Spring Java programs, precisely because they are not sufficiently knowledgeable and skilled in Spring programming to be able to anticipate the inner workings of the framework. I wouldn't have written this if I were not among this majority of developers who perform better in non-Spring programs.

**You're probably better off avoiding Spring unless you already happen to be a team of Spring _experts_.**

## Appendices

### Appendix A: The "flatness" of Spring code

There is a certain "flatness" to Spring programs. They lack structural glue code: Spring Beans are added to the Spring Context and the framework takes responsibility for making sure they are invoked in the right place at the right time, without the author needing to know the details. A request filter defined in one class has no syntactic connection to request handlers defined in another class, and yet they lie on the same code path. If you arrive at one while reading the code, there is no way to statically navigate to the other or to see that it is affecting the overall program.

This is great in a way, because you don't need to write any glue code, or understand how to structure your program. The tradeoff is that the missing structural glue can be very useful for readers who want to step through the program in order to statically analyse it in their mind in order to make sense of it.

### Appendix B: Netflix's use of Spring

In the right context, it might be possible to disregard Spring's detrimental impact on reader experience. That context is microservices, which is an architecture that Netflix famously pioneered.

In a small, self-contained program such as a microservice, it might be okay that a reader has to go through all of its classes to understand the program, because there just aren't that many classes to go through (and obviously, good names can obviously help readers find what they're looking for more quickly). The use of Spring in Netflix is _strategically_ aligned with their architecture. Microservice architectures have fallen out of favour recently, in my opinion for good reasons.

Spring's weakness in reader experience can be mitigated somewhat by attempting to read and understand only small programs, however, as the industry has learned in recent years, defining an essentially complex service as a distributed system of individually small programs brings its own challenges, which are written about plentifully elsewhere.

---
Created on 2025-12-05

Updated on 2025-12-05
