# Call Log

Simple Android app that shows your call log.

This app will refresh your call log list without the need of any user input.

## Setup

This project was built using Android Studio, Gradle and Kotlin. They all should seamlessly work
together, so it should be as easy as cloning the project, and importing it into Android Studio.

## High Level Architecture

From a high level the app is a single activity and single fragment app. The fragment could be
removed to be just one activity, but fragments are a good default.

The fragment's holds a view model of the CallLog (a list of call log entries) and a view adapter.
The fragment's main responsibility is managing the interaction from the view model and the adapter.
The fragment starts a subscription to the changes of the view model's live data and when it changes
it will let the adapter know what new data it's gotten. The fragment is also in charge of
requesting permissions if the view model tells it that it failed to get the logs, and telling the
view model if the permissions were granted.

The view adapter will get new data from the fragment and properly update the view for the user
with any new data. It is the adapter's responsibility to transform the internal representation
of a call log entry, into its displayable forms (i.e., putting the data in its view's text field).

The view model is intentionally simple, it exposes a non-mutable LiveData which is backed by a
mutable live data. It also tells the live data when the fragment has informed it of a permission
grant.

The live data is where most of the work lies. The live data holds a sealed class of either a
successful CallLog or an error. This will inform any subscribers (the fragment) if it failed to get
the call log data due to permissions being not granted. For the rest of this explanation we will
assume permissions are granted since that is the most interesting case. Whenever the live data
becomes active (it has an active subscriber) it will immediately trigger a coroutine to load the
call logs, and inform any subscribers (the fragment) of the change. Without waiting for the
coroutine to finish, it will start listening to any changes in the call log content URI. When
changes occur it will again re-trigger a load of the data in a coroutine which will inform the
subscribers of any changes to the call log. Whenever the view model goes inactive (no active
subscribers), we cancel any current load coroutine jobs, and unsubscribe from the call log content.

The actual loading of data is fairly uninteresting, it queries the Content URI, grabs the first 50
rows, and parses them into a data class.

## Chosen Frameworks

### Recycler View + DiffUtil

This project uses `RecyclerView` over a simple `ListView` since it forces better patterns that
wil lead to better UI performance.

I used DiffUtil as a simple way to tell a `RecyclerView` what rows got moved/updated.
Whenever a new call is logged I didn't want the `RecyclerView` to have to re-render and update
all the rows, instead I wanted it to simply know the difference between the new and the old list
and properly only create those new rows, and push the rest down.

### Mockk

I chose `Mockk` as my mocking framework since it is built with Kotlin in mind, rather than the more
popular `Mockito` which was built more around Java. They both would fit my purposes, but I wanted
to use a library that fit the chosen language better.

### Ktlint

A standard Kotlin linter because I didn't want to bikeshed with myself over style.

## TODOs

* Test, Test, Test

  While there are some unit tests to verify that data is parsed correctly from the contentResolver
  there aren't any tests to verify that the parsed data is then used/shown correctly. There is
  also no tests to verify the LiveData/ViewModel/Fragment interactions.

  This app is seriously lacking at least one full happy path UI test, or at least a robolectric
  test.

* Pull in a DI

  In the past I have used Koin so I would probably pull that in were this app get any bigger.
  This should also help testing as I can DI mocks easier.

* Investigate a better way to scope coroutines, and cancel them when updated.

  Currently `CallLogLiveData` has to manually cancel the co-routine whenever a change happens to
  prevent an out of sync update. Ideally this would be handled by the framework...somehow? My
  concern is that this might not be super intuitive and create a bug when it eventually gets
  refactored/moved around. Similarly, `CallLogLiveData` also has to cancel this job when it goes
  inactive.

## Outstanding questions
* How should the app display a call log without a number (i.e., the number is private)?

  Currently it just shows no number. Alternatively we could say "PRIVATE".