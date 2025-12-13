# Let your code tell the truth

This page briefly describes the eponymous motto I use while refactoring code.
I've used this motto for several years and my (unempirical) feeling is that every time I've decided to ignore it, I later end up realising that I'd have been better off sticking to it all along.

Imagine yourself in the following, relatively common scenario: You've written some production code which all passes the tests you want to write for it. While looking back over it though, you decide that in its current state, it may bewilder future readers, because in some places it's a bit confusing and/or verbose. Some refactoring is in order. If all goes well, you'll produce something concise and elegant, and you'll be done. However, things don't always go well. In this case, you've found that you can't get the code into a satisfying shape. No matter what names you use or how you shuffle the code between methods, the code you've written just doesn't quite cohere. It is in this moment of frustrated introspection that my motto is helpful.

Concretely, this motto is about resisting the temptation to shuffle code around, or to come up a concise name for a method that clearly has too many side effects. Instead, embrace that badness and let your code tell the truth about itself. You'll probably have better ideas tomorrow after sleeping on it, and leaving the code in its unadulterated form will likely make it more malleable when you return later. And if you never need to return, then all the better to have not spent any more time on it.

So there it is. Happy hacking!

---
Created on 2025-12-13
