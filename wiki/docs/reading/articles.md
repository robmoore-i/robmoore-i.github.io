# Articles

### [Lockhart's Lament](https://worrydream.com/refs/Lockhart_2002_-_A_Mathematician's_Lament.pdf)

### [Barrels and Ammunition](https://www.conordewey.com/blog/barrels-and-ammunition/)

### [Things you should never do, Part 1](https://www.joelonsoftware.com/2000/04/06/things-you-should-never-do-part-i/)

Rewriting from scratch is almost never a good idea.

### ["Founder mode" and the art of Mythmaking](https://charity.wtf/2024/12/17/founder-mode-and-the-art-of-mythmaking/)

### [Distributed Systems Shibboleths](https://jolynch.github.io/posts/distsys_shibboleths/)

### [DevEx: What Actually Drives Productivity](https://queue.acm.org/detail.cfm?id=3595878)

### [A human-centered approach to developer productivity](https://ieeexplore.ieee.org/ielx7/52/9994072/09994260.pdf)

### [The SPACE of developer productivity](https://queue.acm.org/detail.cfm?id=3454124)

### [Write better error messages](https://wix-ux.com/when-life-gives-you-lemons-write-better-error-messages-46c5223e1a2f)

Describes the failures of bad error messages, and also gives an opinionated view on what good looks like, which I think is a pretty good one. If all error message writers learned the lessons described, the world's software would be significantly better.

### [Questioning vs Asking](https://candost.blog/questioning-vs-asking/)

Asking questions from a place of genuine curiosity has been a game changer for my own learning, and for my work relationships. Sometimes, as you're about to ask a question (e.g. in an email or a message) it is as simple as tuning into your own thoughts and adjusting your intention, without even changing the words you're using. In doing so, we can tap into the part of our selves that is focused on learning, rather than winning.

### [The Plan](https://web.mnstate.edu/alm/humor/ThePlan.htm)

### [Thinking about the complexity of the Kubernetes ecosystem](https://erkanerol.github.io/post/complexity-of-kubernetes/)

I like this empathic treatment of Kubernetes from the perspective of new users, experienced users, and of the its creators. I think this empathy bears applicability to other domains as well, such as build tools.

### [Using Gradle to download and run anything](https://jonnyzzz.com/blog/2016/03/06/gradle-all-maven-runner/)

I like this little trick. I wish the official Gradle documentation had more things like this. I should add it as a [sample](https://docs.gradle.org/current/samples/index.html).

### [Project Loom and Thread Fairness](https://www.morling.dev/blog/loom-and-thread-fairness/)

### [Thinking about the complexity of the kubernetes ecosystem](https://erkanerol.github.io/post/complexity-of-kubernetes/)

I like this article for its balanced take on the kubernetes experience, and the project's role in the tooling ecosystem for deploying applications on cloud services.

### [CUPID - for joyful coding](https://dannorth.net/2022/02/10/cupid-for-joyful-coding/)

Joyful is a great word to describe the feeling you can give to yourself and your colleagues by writing code with care. Read the [corresponding paragraph](https://dannorth.net/2022/02/10/cupid-for-joyful-coding/#joyful-software) of the article.

The CUPID properties themselves are great. Something I like is that even for one class in an object-oriented design, you can look at it through the lens of these values. I don't find that the same is always true for the SOLID acronym.

### [LMAX disruptor](https://lmax-exchange.github.io/disruptor/disruptor.html)

### [Distributed Systems Shibboleths](https://jolynch.github.io/posts/distsys_shibboleths/)

I really liked the positive shibboleths here. In particular:

- We made the operation idempotent
- The system makes incremental progress
- Every component is crash-only

These are all great qualities for any piece of software to have: Idempotent operations, incremental progress, and uncompromising, early, failures.

### [Chesire Yeomanry](https://chrisseaton.com/army/)

Tech perhaps could learn from the Yeomanry.

1. Mission command

> People ... have a mistaken idea that the Army is rigidly hierarchical. Yes, it’s always extremely clear who’s in command and we have etiquette, symbols and ceremonies to reinforce this, but the Army and especially the Yeomanry is actually excellent at integrating everyone’s input and empowering people at all levels. It’s sacrosanct that I tell the people in my Squadron what I want them to achieve, and not how to achieve it. They take a goal from me and then use their own initiative to make it happen. This is ‘mission command’ and violating it and micromanaging my Yeomen is a real taboo. If you try to tell a Yeoman how to cross a piece of ground in their vehicle rather than telling them where to get to they’ll certainly let you know what their job is and what your job is. I feel like tech could really learn something from this.

2. The importance of explaining why

> ... People think the Army is all about just being told what to do and doing it without question. Really, the Army is fastidious about telling people why they’ve been told to achieve something. In our way of delivering orders we emphasise explaining the context two levels up. I may tell my soldiers to raid a compound, but I would also tell them that the reason for this is to create a distraction so that the Colonel can divert the enemy away from a bridge, and that the reason the Brigadier wants the Colonel to divert the enemy is so that the bridge is easier to cross. Not only do the soldiers then know why it’s important to raid the compound (so that others can cross the bridge), but they know that if for some reason they can’t raid the compound, creating any other diversion or distraction will do in a pinch, and if they can’t do that they can still try to do something to make it easier to cross the bridge. It lets everyone adapt to change as it happens without additional instruction if they aren’t able to get in touch with me. Again I think tech could possibly learn from that.

### [The Tyrany of 'what if it changes?'](https://chriskiehl.com/article/the-tyranny-of-what-if-it-changes)

I don't like the Java-bashing, but this article touches on an important skill of good software engineers - accurately estimating the cost of hypothetical, future change.

Example 1: An essential implementation of some specification in your stack is notoriously slow at releasing security patches. It might make sense to decouple from this tool quickly. This might be your choice of JDK.

Example 2: The S3 bucket path for storing some data is hard coded. You probably don't need to care about the risk of this changing. If it needs to change, you will deal with it, no problem.

### [Founders should think about channel/offer fit instead of product/market fit](https://jakobgreenfeld.com/channel-offer-fit)

### [BashFAQ/045 - Mutual exclusion in Bash](http://mywiki.wooledge.org/BashFAQ/045)

In your darkest hour, you may need this.

### [Why I like Java](https://blog.plover.com/prog/Java.html)

I don't like this article, but I like this cherry-picked excerpt, because it describes a programming feeling that I like: The feeling that your software is a just matter of time. Of course, I do take great care to see that I am doing things in the right way.

>  In Haskell or even in Perl you are always worrying about whether you are doing something in the cleanest and the best way. In Java, you can forget about doing it in the cleanest or the best way, because that is impossible. Whatever you do, however hard you try ... the only thing you can do is relax and keep turning the crank until the necessary amount of code has come out of the spout.

### [Minsky moments in Venture Capital](https://pivotal.substack.com/p/minsky-moments-in-venture-capital?utm_source=url)

---
Created on 2022-05-28

Updated regularly
