# Pattern Demos

Pattern Demos are a collection of processes, dialogs, code or other snippets, which can be useful in your projects.

## Demo

### Admin Task

Use an Admin Task to catch errors in unattended backend-jobs (e.g. cron-jobs). In case of errors,
an admin role gets a task with the results and can decide whether the job should be retried or skipped.

#### TODO

* Fix bugs
* Make better example with a system task producing an error, which is fixable somehow
* Add description either in README.md or directly in process


### Cronjob

Use this cron-job pattern for all your cron-jobs to make them startable manually and in case of
manual start or errors, create an admin task to let the admin role decide how to continue.

#### TODO

* Prepare two smal cron jobs
* Reduce as max as possible
* Add description either in README.md or directly in process
* Describe, why we write jobs in Java

### Placeholder Evaluation

* Rewrite to a more understandable example
* Make simpler without the need of so many additional class
* Add description either in README.md or directly in process

### Primefaces Extensions

Use these examples to see how Primefaces Widgets can be customized using the Primefaces client-side API of widgets.

* Add comments to Javascript, reduce what is not needed to show the basic idea
* Add description either in README.md or directly in process

## Setup

This component is a repository for valuable patterns and demos. Typically they must be adapted to your
project situation. Please copy and adapt the pattens and examples that you want to use directly to your project.

## TODO

* Rename UI beans, pattern: XyzCtrl
* Extract (and reuse) Locking into it's own topic
* Provide JUnit test for Locking