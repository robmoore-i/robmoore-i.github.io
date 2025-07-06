# WIP: Governance is not a quality strategy

In this page, I rehash Deming's truism:

> Inspection does not improve the quality, nor guarantee quality. Inspection is too late. The quality, good or bad, is already in the product. As Harold F. Dodge said, “You can not inspect quality into a product.”

## Problem: Low quality work

Everyone who produces software has to face the reality of shipping bugs. However, some organisations seem to suffer disproportionately, and they understandably want to figure out how they can change things in order to produce higher quality software that sells better and is stickier amongst existing customers.

In this page, when I talk about quality with respect to software, I am talking primarily about bugs, but also design, usability, and utility. Low quality can then refer to buggy, ugly or unusable software. Bugs in this context includes any behaviours that customers unexpectedly interpret as bugs, due to their uselessness or inconvenience.

Much thought and money has been injected into coming up with a reproducible process for building high quality software, yet the vast majority of efforts in organisations of all sizes have yielded paltry benefits, if any at all. This is because most organisations rely on false intuition, from which they arrive at the below non-solution.

## Not a solution: Quality gates

Leaders facing quality problems, who are not aware of Deming's truism, use their intuition to tackle the problem. They reason that by adding quality checks and inspections during the process of building software, they will be able to ensure that only high quality software gets deployed and impacts their customers. In my experience, this line of thinking is attractive to people at all levels of experience. It all seems so simple.

Maybe you believe, quite plausibly I think, that the quality of work every engineer does lies on a normal distribution. The mis-step is in believing that you can eliminate all the work on the lower end the distribution, retaining only the work on the upper end. Unfortunately, software engineering work doesn't slice like that. Producing code is not like producing uniform iron screws, where you can throw out the bad and keep the rest. It's more like producing essays, or music compositions - the work is deeply integrated with itself, and it is impossible to untangle the moments of acuity from the moments of dimness. To make an analogy, I wouldn't be able to compose music like Hans Zimmer just by applying the right set of quality gates.

There are varying degrees to which different engineering organisations get this wrong:

- Very wrong: Introduction of a manual testing / approval step.
- Another effort pursuing the same incorrect idea: Mandatory code review for all changes.
- Another effort pursuing the same incorrect idea: All changes must include automated tests and code coverage must be 95%.

I'm not saying that these are necessarily bad ideas in any context, only that they are not solutions to the problem of producing higher quality software. The incorrect idea common to these ideas is that adding quality checks improves quality.

To paraphrase Deming again, inspection is too late! Whatever level of quality the work is, _it's already done_. Nothing you do after the work has happened can increase its quality. To improve quality, you must improve the work done.

## Real solutions require engineers to produce higher quality work

I've listed these in order, ranked by my opinion of them, starting with the 'best', as in most likely to have a positive impact, and proceeding down to 'least best, but still good'.

### Making it easier for your engineers to do better work

I hope you saw this coming. If you want better work to happen in your engineering organisation, and you don't believe in miracles, then you need to create an environment in which people are capable of simply doing better work.

#### Allowing engineers to improve product quality when they know it's needed

A significant own-goal that organisations often score is that when they know about a quality problem, and they also have the skills available to fix it, and they have time to do so, they often simply choose to not fix the problem. This isn't the decision of one individual (a key part of the problem), but rather the decision of the amorphous "organisation". To frustrated, paying customers, this is inexplicable and infuriating, yet it happens in many industries, from tech to finance.

It's this simple: When quality problems are identified in your software, you need to fix them! If you don't fix the quality problems you know about, how can you possibly hope to improve the quality of your organisation's software? This should be obvious, and yet, I think it's hugely differentiating amongst software engineering organisations. I believe the reason for this is that you can't easily imitate this culture by adopting some acronym or "framework", and you can't buy it via any tool or certification. It is cultural and it comes from the leadership. 

This kind of culture requires a team to value individuals and interactions over processes and tools. I can empathise with teams that struggle with this. I find it easy to imagine struggling to justify a people-centred approach to dispassionate number-crunchers in an age where the available tools are so powerful and accessible.

I think the underlying principle here is that if you want to improve quality, you have to care about quality. If you don't care about quality when it comes to your decisions, don't expect to produce quality by serendipity!

#### Streamlining processes to reduce interruptions and distractions

To solve a problem at the edge of my abilities, I need to be able to focus on it, and I would venture to say that most people are basically the same in that regard. We need to be able to focus and concentrate in order to solve the hardest problems that we're capable of solving.

Giving engineers the gift of focus also gives them the ability to solve problems that otherwise might prove too difficult for them. 

#### Removing and mitigating high-friction developer experiences

Shortening feedback loops to allow developers to iterate more, which gives them (a) more chances to understand the problem they're solving (b) more chances to perfect their solution. In this way, an engineer working on a problem where they can iterate freely, seems to be smarter, faster and completely better than the same engineer working on a problem where iteration is painful, difficult, slow or flaky. In this way, you can improve the skills of engineers just by speeding up their feedback loops.

The ideal feedback loop is of course an automated test. Knowing to use automated tests is a skill that requires practice, but if you create the right culture, you should be able to drop an engineer into a culture of testing and have them adopt it as their own fairly quickly.

#### Introducing tools that help engineers do better work

This is an approach known and used by all engineering organisations, simply because it is the easiest to adopt. It's a good and necessary part of a holistic approach.

Where organisations get this wrong is in expecting too much from tools. Tools are important, but their effectiveness relies on a strong foundation built by other drivers of quality that I mention elsewhere here: Having a team of skilled engineers, unencumbered by inefficient bureaucracy, and trusted enough to actually implement the quality improvements they identify. Adding the best and most advanced developer tooling to a team of coasters who don't care about customers will not do much for your software's quality.

#### Introducing processes that help engineers do better work

In my experience, most processes given to engineers are overheads without much value, but I've no doubt that there are processes you can introduce which genuinely to result in better work. 

One process I've been introduced to at my current team (Gradle's Develocity team) surprised me by its high value. If you're already familiar with it, maybe this is cringey to you, but I found that writing detailed specification documents has often paid for itself in catching problems early and resulting in well-thought-out solutions and excellent implementations for complicated features. At some point I'd like to write an article about the specification documents written by my colleague who is best at it - the level of detail, the length, and the format. But I digress.

### Helping your existing engineers to become more skilled

#### Pair programming

A great way to transfer skills and create an organisation full of your best few people.

#### Empowering your known best engineers to set the engineering culture

As above.

### Hiring more skilled engineers

No matter what you do, a team entirely staffed by cheap, middle-of-the-road developers might not be able to acquire the skills you need them to, to deliver the quality of work you need delivered, in the time frame that you need it delivered in. You might need to hire new people, who are experienced, skilled, and unfortunately, perhaps more expensive.

The good news is that once you have a culture of quality, excellence and care, you can add people from less performant organisations into it and, like [Herbert's ants on a beach](https://rs.io/herbert-simons-ant/), they should adopt the practices and culture they are immersed in.

#### Probation periods are useful

I think it's a good sign when an engineering organisation isn't afraid to let people go when it clearly isn't a good fit. Sometimes, a person has great credentials, super relevant experience, they aced the interview, but somehow they never manage to take off on the job. This is what probation periods are for, and when a company never uses it, I think it's a bad sign. Such a company will just go on accumulating median performers forever until the organisation itself is a median performer (a fate you don't want in this economy).

---
Created on WIP

Updated on WIP
