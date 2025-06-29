# WIP: Governance is not a quality strategy

In this page, I rehash Deming's truism:

> Inspection does not improve the quality, nor guarantee quality. Inspection is too late. The quality, good or bad, is already in the product. As Harold F. Dodge said, “You can not inspect quality into a product.”

## Problem: Low quality work

There's a lot of rubbish software out there at the moment. Large, well-funded organisations are often guilty of producing low quality software, which huge numbers of people have to suffer from. In the recent past low quality software has been fatal and ruined many lives. Much thought and money has been injected into coming up with a reproducible process for building high quality software, yet the vast majority of efforts in organisations of all sizes have yielded paltry benefits, if any at all. This is because most organisations rely on false intuition, from which they arrive at the below non-solution.

## Not a solution: Quality gates

Leaders facing quality problems, who are not aware of Deming's truism, use their intuition to tackle the problem. They reason that by adding quality checks and inspections during the process of building software, they will be able to ensure that only high quality software gets deployed and impacts their customers. In my experience, this line of thinking is incredible attractive to people at all levels of experience. It all seems so simple.

There are varying degrees to which different engineering organisations get this wrong, all around the same theme:

- Very wrong: Introduction of a manual testing / approval step.
- Another effort pursuing the same incorrect idea: Mandatory code review for all changes.
- Another effort pursuing the same incorrect idea: All changes must include automated tests and code coverage must be 95%.

The incorrect idea is that adding quality checks improves quality. To paraphrase Deming, Inspection is too late! Whatever level of quality the work is, _it's already done_. Nothing you do after the work has happened can increase its quality. To improve quality, you must improve the work done.

Maybe you believe, quite plausibly I think, that the quality of work every engineer does lies on a normal distribution. Your misstep is in believing that you can eliminate all the work on the lower end the distribution, retaining only the work on the upper end. Unfortunately, software engineering work doesn't slice like that. Producing code is not like producing uniform iron screws, where you can throw out the bad and keep the rest. It's more like producing essays, or music compositions - the work integrates with itself. I won't be able to compose music like Hans Zimmer just by applying the right set of quality gates.

## Real solutions require engineers to produce higher quality work

I've listed these in order, ranked by my opinion, starting with the 'best', as in most likely to have a positive impact.

### Making it easier for your engineers to do better work

I hope you saw this coming. If you want better work to happen in your engineering organisation, and you don't believe in miracles, then you need to create an environment in which people are capable of doing better work.

#### Allowing engineers to improve product quality when they know it's needed

A significant own-goal that organisations frequently score is that when they know about a quality problem, and they also have the skills available to fix it, and they have time to do so, they often simply choose to not fix the problem. This isn't the decision of one individual (a key part of the problem), but rather the decision of the amorphous "organisation". To frustrated, paying customers, this is inexplicable and infuriating, yet it happens in many industries, from tech to finance.

It's this simple: When quality problems are identified in your software, you need to fix them! If you don't fix the quality problems you know about, how can you possibly hope to improve the quality of your organisation's software? This should be obvious, and yet, amazingly, it is hugely differentiating amongst software engineering organisations. The reason for this is that you can't easily imitate this culture by adopting some acronym or "framework", and you can't buy it via any tool or certification. If you're familiar with the original Agile manifesto, you'll know this as valuing "individuals and interactions over processes and tools".

I think the underlying principle here is that if you want to improve quality, you have to care about quality. If you don't care about quality when it comes to your decisions, don't expect to produce quality by serendipity!

#### Streamlining processes to reduce interruptions and distractions

To solve a problem at the edge of my abilities, I need to be able to focus on it, and I would venture to say that most people are basically the same in that regard. We need to be able to focus and concentrate in order to solve the hardest problems that we're capable of solving.

In my experience, bringing engineers into frequent synchronous meetings is bad for productivity. So much can be communicated asynchronously, and for me, it has been a game-changer to come into a team where that is the norm, and meetings are not the default.

#### Removing and mitigating high-friction developer experiences

It should be somebody's job to make everybody more productive and keep them productive by solving problems that distract them for value-adding work.

#### Introducing tools that help engineers do better work

This is an approach known and used by all engineering organisations, simply because it is the easiest to adopt. It's a good and necessary part of a holistic approach to producing high quality software.

Where organisations get this wrong is in expecting too much from tools. Tools are important, but their effectiveness relies on a strong foundation built by other drivers of quality that I mention elsewhere here: Having a team of skilled engineers, unencumbered by inefficient bureaucracy, and trusted enough to actually implement the quality improvements they identify. Adding the best and most advanced developer tooling to a team of coasting lifers who don't care about customers will do precisely nothing for your software's quality.

### Helping your existing engineers to become more skilled

#### Pair programming

A great way to transfer skills and create an organisation full of your best few people.

#### Empowering your known best engineers to set the engineering culture

As above.

### Hiring more skilled engineers

No matter what you do, a team of offshore developers from <budget-friendly developing nation> might not be able to acquire the skills you need them to, to deliver the quality of work you need delivered, in the time frame that you need it delivered in. You might need to hire new, local people, who are experienced, skilled, and unfortunately, perhaps more expensive.

#### Probation periods are useful

I think it's a good sign when an engineering organisation isn't afraid to let people go when it clearly isn't a good fit. Sometimes, a person has great credentials, super relevant experience, they aced the interview, but somehow they never manage to take off on the job. This is what probation periods are for, and when a company never uses it, I think it's a bad sign. Such a company will just go on accumulating median performers forever until the organisation itself is a median performer (a fate you don't want in this economy).
