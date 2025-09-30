# Complex domain models versus complex black boxes

Interfaces for solving complex problems should be based on a representatively-complex model of the problem domain, rather than complex black boxes with powerful, internal behaviour.

Interfaces which model the problem domain more deeply provide more flexibility and reuse than interfaces which provide only superficial models of the problem domain and have most of the complexity hidden within black boxes.

This is an abstract idea and I think it's true on many levels of interfaces. It's true at the level of GUIs, APIs, configuration files, and abstractions in code. The same idea is at work in all these places - fundamentally, these are all user interfaces.

It's easy to design an interface that supports the most common use-cases, but to create an interface which gracefully also supports the long tail of less common use cases, it helps when an interface is built on a minimal, useful model of the problem domain. When an interface is made like this, adding new functionality, even for niche use-cases, becomes natural and obvious. A valuable skill is knowing how to factor an existing design in order to introduce a new concept to it.
