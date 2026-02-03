# Let your code tell the truth

This page briefly describes the eponymous motto I use while refactoring code.
I've used this motto for several years and it seems to me that every time I've decided to ignore it, I later end up realising that at best I wasted my time, but more commonly, that I'd actually have been better off letting my code tell the truth.

Imagine yourself in the following, relatively common scenario: You've written some code and have reached a point where you think some refactoring is in order. If all goes well, you'll refactor the code into something concise and elegant, and you'll be done. However, things don't always go well. In this case, you've found that you can't get the code into a satisfying shape. No matter what names you use or how you shuffle the code between methods, the code you've written just doesn't quite cohere. You may be tempted to obscure the inadequacies of your design behind dubious abstractions with artistic names. It is in this moment that I remind myself of this motto ('Let your code tell the truth'). 

Concretely, this motto is about resisting the temptation to introduce dubious abstractions, or to come up a clever, concise name for a method that clearly has too many side effects. Instead, embrace that badness and let your code tell the truth about itself. You'll probably have better ideas tomorrow after sleeping on it, and leaving the code in its unadulterated form will make it more malleable for when you return later. And if you never need to return, then all the better to have not spent any more time on it.

So there it is. Happy hacking!

---
Created on 2025-12-13
