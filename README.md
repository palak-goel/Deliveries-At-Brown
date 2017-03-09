# cs0320 Term Project

**Team Members:** _Fill this in!_

**Project Idea:** _Fill this in!_

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Project Requirements
_This portion of the README is due March 8th (see the project handout)!_

1.     Project Description
Our project is to create a general-purpose delivery application for students at Brown. At a high-level, anyone would be able post a delivery request and anyone would be able to fulfill a delivery. The main problem our program attempts to solve is exploiting students’ laziness to walk to a certain location when someone near that location can bring it to them. 
    For example: a student in Keeney does not feel like walking to the blue room, so they post a delivery request to the website, with their offered wage. Soon after, a student leaving class on the Main Green wants to make a quick profit before heading back to their dorm. They hop on the site, which obtains their current location and asks for their destination and amount of free time before ranking potential deliveries based on proximity to current and end location. The student could then select any number of deliveries and fulfill them.

2.   User perspective
On the login page, users have the option of logging in or creating a new account. Once they are logged in, they are brought to a homepage where they can request a delivery or choose to deliver items. Under both options is a list of pending deliveries. Each delivery option in the list has a “deliver this” tag, so someone on the homepage could pick up a delivery easily. However, if they choose the deliver items link, the software will generate smart suggestions based on their current location, end-destination, and the time they have (using a map api). To request a delivery, simply input what you want, the address it is located at (this will utilize autocorrect), where you are located, and how much you are willing to pay. After that, it gets posted to the running delivery list for anyone to pick up. In addition, once you start a delivery, it will be listed under a newly generated “pending deliveries tag.” Once you click on “delivery complete” and the requester has paid for their items, the deliverer and requester can rank one another (yet another aspect of delivery option ranking).

3. Requirements + User Validation
Streamline peer-to-peer item procurement using an interactive web-app.
People we surveyed agreed that this was a good problem to solve. They acknowledged that it would work well for a college campus since people could sign up and fulfill orders very easily. Furthermore, students we talked to confirmed how easily and useful it would be for other students who are in an area or leaving a location to pick something up from there.
Allow for the posting of items by a requestor
Allow for deliverers to select any item they want. Deliverers should be able to pick up multiple tickets at once.
Allow for deliverers to view a smart recommendation of potential deliveries
We talked to users, and most agreed that deliverers should be able to do 3 and 4. If a deliverer is about to leave a location and pick something up, they should be able to quickly scan through open tickets and pick one (or more) up. On the other hand, if nothing is open at the time, the system should be smart enough to notify the deliverer when a relevant ticket is added to the queue. 
Utilize location, time, wage, and ranking to generate recommendations for the deliverer.
Users agreed that these would be the main components in recommending a ticket to the deliverer. Some people we talked to wanted even more metrics to factor into the smart suggestion (for example, weather, or likelihood of a line) but by keeping it simple (just these four categories) we make it easy for anyone to submit a ticket with only the relevant information.
Allow for deliverers and requestors to communicate once a delivery is picked up
All users agreed that this was a very important feature, even if it wasn’t baked into the app. Especially since people might have to move around or swipe others in, it’s important to have an open line of communication. A chat system is a possible solution to this issue.
Allow for some notion of payment (even if it’s by an initial API) to ensure that
Delivery seekers automatically pay part of the order once it’s picked up
Deliverers are paid in full once the order is fulfilled
Offers a flat payment fee, but a user is allowed to set it higher for faster deliveries
Hold deliverers accountable if they mess up or abandon an order intentionally.
We asked users what scope we should take the payment aspect of this app, which would definitely be key to actually launching it, but wouldn’t necessarily be as crucial/interesting for the scope of the project. Survey respondents indicated, however, that payment could be used as a checks and balances to ensure good behavior on the parts of requesters and deliverers, and advised us to integrate some form of payment in the final process -- even if it’s just a “dummy” API that lets us see how it would look. A cool aspect of the payment here would be that requesters should be willing to indicate they can pay more, which would make their ticket bubble to the top.
Allow for easy handling of disputes
Many survey respondents indicated that complaints could become an issue if deliverers picked up the wrong order, did not deliver on time, or if requesters moved / made false accusations. An easy rating system that allows pictures to be a part of any complaints that are filed will allow for maximum transparency.

One feature that respondents indicated would be useful was requesters being automatically matched with a deliverer, like most other sharing economy apps which automatically match people. The reason we chose not to make this a requirement is that it doesn’t fit the use case of our app, which is really targeting requesters/deliverers who want something at their convenience. Furthermore, deliverer preferences can be somewhat unpredictable, and they might not always appreciate the automatic selections being made. The app makes no guarantees about having deliverers available anyway, so it wouldn’t make much sense.
Another feature that respondents indicated might be useful was the ability to utilize the app without creating any user profile. However, we disregarded this idea as it could lead to security risks and to poorly rated users and deliverers continuing to use the application.

## Project Specs and Mockup
_A link to your specifications document and your mockup will go here!_

## Project Design Presentation
_A link to your design presentation/document will go here!_

## How to Build and Run
_A necessary part of any README!_
