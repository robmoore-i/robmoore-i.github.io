# Governance is not a quality strategy

In this page, I rehash Deming's truism:

> Inspection does not improve the quality, nor guarantee quality. Inspection is too late. The quality, good or bad, is already in the product. As Harold F. Dodge said, “You can not inspect quality into a product.”

## Problem: Low quality work

While working in a couple of unnamed global retail banks, and later in other contexts, I witnessed and took part in efforts to improve and control quality by applying what is arguably the most primitive and fundamental tool available to leaders - governance. By governance, I mean the introduction of constraints in the form of rules and guidelines. Sometimes the impacts of these efforts looked like success at first glance, but the result never seemed to be better software, faster feedback loops, or happier customers. Such efforts reliably resulted in _less_ software development, but of roughly the same quality.

In this page, when I talk about quality with respect to software, I am talking primarily about bugs, but also design, usability, and utility. Low quality refers to buggy, ugly or unusable software. Bugs in this context includes any behaviours that customers unexpectedly interpret as bugs, due to their uselessness or inconvenience.

Much thought and money has been injected into coming up with a reproducible process for building high quality software, yet most efforts seem to have yielded paltry benefits, if any at all. This is because we are prone to relying on false intuition, using which we risk arriving at the following non-solution.

## Not a solution: Quality gates

It's straightforward to reason that by adding quality checks and inspections during the process of building software, you will be able to ensure that only high quality software gets deployed and impacts customers. In my experience, this line of thinking is attractive to people at all levels of experience.

I think it's quite plausible to think that the quality of work every engineer does lies on a normal distribution. However, it would be an error to believe that you can eliminate all the work on the lower end the distribution, retaining only the work on the upper end. Software development work doesn't slice like that. Producing code is not like producing uniform iron screws, where you can throw out the bad and keep the rest. It's more like producing essays, or music compositions - the work is deeply integrated with itself, and it is impossible to untangle the moments of acuity from the moments of dimness. To make an analogy, I wouldn't be able to compose music like Hans Zimmer just by applying the right set of quality gates.

There are varying degrees to which different engineering organisations get this wrong:

- Painful and wrong: Manual testers check and approve all changes.
- Less painful, but still pursuing the same incorrect idea: Mandatory code review for all changes.
- A cool and well-intentioned effort, but still pursuing the same incorrect idea: All changes must include automated tests and code coverage must be 95%.

I'm not saying that these are necessarily bad ideas in any context, only that they are not solutions to the problem of producing better software. The incorrect idea common to these ideas is that adding quality checks improves quality.

To paraphrase Deming again, inspection is too late! Whatever level of quality the work is, _it's already done_. Nothing you do after the work has happened can increase its quality. To improve quality, you must improve the work done.

## Real solutions require engineers to produce higher quality work

This is the core of my opinion on this problem: **Real solutions require engineers to produce higher quality work.**

I've listed below some ideas that I consider to be real solutions. The list is in order, ranked by my opinion of them, starting with the 'best', as in most likely to have a positive impact.

### Making it easier for your engineers to do better work

One well-documented way to make better work to happen is to create an environment in which people are capable of simply doing better work. A person's output is not only about them individually, it's also about their surroundings and what they see others' around them doing.

#### Allowing engineers to improve product quality when they know it's needed

A significant own-goal that I've seen a few organisations score is that when they know about a quality problem, and they also have the skills available to fix it, and they have time to do so, they often simply choose to not fix the problem. This isn't the decision of one individual (a key part of the problem), but rather the decision of the amorphous "organisation". To frustrated, paying customers, this is inexplicable and infuriating, yet it seems to happen quite often.

It's this simple: When quality problems are identified in your software, you need to fix them! If you don't fix the quality problems you know about, how can you possibly hope to improve the quality of your organisation's software? This should be obvious, and yet, I think it's hugely differentiating amongst software engineering organisations. I believe the reason for this is that you can't easily imitate this culture by adopting some acronym or "framework", and you can't buy it via any tool or certification. It is cultural and it comes from the leadership. 

This kind of culture requires a team to value individuals and interactions over processes and tools. I can empathise with teams that struggle with this. I find it easy to imagine finding it hard to justify a people-centred approach to dispassionate number-crunchers, especially in an age where the available tools are so powerful and accessible, as they are now.

I think the underlying principle here is that if you want to improve quality, you have to care about quality. If you don't care about quality when it comes to your decisions, don't expect to produce quality by serendipity!

#### Streamlining processes to reduce interruptions and distractions

To solve a problem at the edge of my abilities, I need to be able to focus on it, and I would venture to say that most people are basically the same in that regard. We need to be able to focus and concentrate in order to solve the hardest problems that we're capable of solving.

Giving engineers the gift of focus also gives them the ability to solve problems that otherwise might prove too difficult for them. 

#### Removing and mitigating high-friction developer experiences

Shortening feedback loops allows developers to iterate more, which gives them (a) more chances to understand the problem they're solving (b) more chances to perfect their solution. In this way, an engineer working on a problem where they can iterate freely, seems to be smarter, faster and completely better than the same engineer working on a problem where iteration is painful, difficult, slow or flaky. In this way, you can improve the skills of engineers just by speeding up their feedback loops.

The ideal feedback loop is of course an automated test. Knowing to use automated tests is a skill that requires practice, but if you create the right culture, you should be able to drop an engineer into a culture of testing and have them adopt it as their own with a bit of practice and some help from the more established team members.

#### Introducing tools that help engineers do better work

This is an approach known and used by all engineering organisations, simply because it is the easiest to adopt. It's a good and necessary part of a holistic approach.

Where I think organisations get this wrong is in expecting too much from tools. Tools are important, but their effectiveness relies on a strong foundation built by other drivers of quality that I mention elsewhere here: Having a team of skilled engineers, unencumbered by inefficient bureaucracy, and trusted enough to actually implement the quality improvements they identify. Adding the best and most advanced developer tooling to a team of coasters who don't care about customers will not do much for your software's quality.

#### Introducing processes that help engineers do better work

The most common processes adopted in the world of software engineering, at least as far as I've seen, are various incarnations of the Scrum framework. In my opinion, this is usually executed quite poorly and to most teams is more of a hindrance than a help. I know from experience though, that there are processes you can introduce which genuinely to result in better work being done by engineers, and I'm sure there must be others I don't know about, don't know how to use well, or have never been introduced to.

One process I've been introduced to at my current team (Gradle's Develocity team) surprised me by its high value. I found that writing highly detailed specification documents regarding a complex piece of work, has often paid for itself many times over. It does this by inducing focused, end-to-end consideration of the work in various relevant contexts. Introducing the scrutiny of other team members to a specification you've written also improves its quality, and the end result is that you understand the work to be done much more thoroughly than if you were to charge right into it. I'd love to go into more detail on this topic in another article at some point, but I digress.

### Helping your existing engineers to become more skilled

#### Pair programming

Pair programming is a technique with fairly little overall adoption in the industry. It gets a certain amount of hype from its proponents, and I think that is somewhat justified! Having a good experience creating software with a team when you're pairing regularly is truly a great feeling, and the sensation of productivity is awesome. I can highly recommend giving it a go. Of course, it is a difficult technique and I have no doubt that many have tried it with good intentions and an open mind, but still not had success with it. It's not for everyone, but I do think it's worth trying in earnest if you haven't already.

#### Training

As with software engineering processes as I mention above, there is plenty of junk training out there. There are gems though. One of the very memorable times I had in my first year out of university was taking a Udemy course by James Richardson on writing Test-driven Java in Intellij IDEA. It covered a number of topics in just a small course. The IDE fluency I picked up from that course and the attitude I learned to take towards IDE fluency, has stayed with me and benefited me for my whole career so far.

What I mean to say is that there do exist training courses and conferences that are worth sending engineers to. [Citcon](https://citconf.com/) is a conference among them, and I'd love to go every year if I lived in Europe. No doubt there are many others, too numerous for me to possibly list them all.

### Hiring more skilled engineers

No matter what you do, a team entirely staffed by budget-friendly, middle-of-the-pack developers might not have or be able to acquire the skills you need them to, to deliver the quality of work you need delivered, in the time frame that you need it delivered in. You might need to hire new people, who are experienced, skilled, and unfortunately, perhaps more expensive.

The good news is that once you have a culture of quality, excellence and care, you can hire all sorts of people and, like [Herbert's ants on a beach](https://rs.io/herbert-simons-ant/), they will likely adopt the practices and culture they are immersed in.

On this note, I think it's worth acknowledging that probation periods are useful. I think it's a good sign when an engineering organisation isn't afraid to let people go when it clearly isn't a good fit. Sometimes, a person has great credentials, super relevant experience, they aced the interview, but somehow they never manage to take off on the job. This is what probation periods are for, and when a company never uses it, I think it's a worrying signal about the software they produce. Such a company will go on accumulating median performers forever until the organisation itself is a median performer (an undesirable fate in this economy).

---
Created on 2025-07-06

Updated on 2025-07-06
