# Chorong Android

[![Travis branch](https://img.shields.io/travis/poksion/chorong-android/master.svg)](https://travis-ci.org/poksion/chorong-android)
[![Codecov](https://img.shields.io/codecov/c/github/poksion/chorong-android.svg)](https://codecov.io/gh/poksion/chorong-android)

Chorong Android is a little Android utility providing MVP development modules, UI component, Google API wrappers, and extras.

## Sample MVP
[SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) is an entry point to show how implement activity with MVP pattern using chorong-android. It consists of 3 major parts.

### A. View : SampleForPersistence
SampleForPersistence is the Activity and plays role for assembler and View.

#### a. Assembling
#### b. View (as part of MVP - Supervising Controller)

See also : Module

### B. Presenter : SampleForPersistencePresenter

#### a. Business logic
#### b. Related Dependencies

See also : Task

### C. Model : DbManager

See also : Store