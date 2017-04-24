# Chorong Android

[![Travis branch](https://img.shields.io/travis/poksion/chorong-android/master.svg)](https://travis-ci.org/poksion/chorong-android)
[![Codecov](https://img.shields.io/codecov/c/github/poksion/chorong-android.svg)](https://codecov.io/gh/poksion/chorong-android)

Chorong Android is a little Android utility providing MVP development modules, UI component, Google API wrappers, and extras.

## Sample MVP
[SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) is an entry point to show how implement activity with MVP pattern using chorong-android. It consists of 3 major parts : [SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java), [SampleForPersistencePresenter](samples/src/main/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenter.java) and [DbManager](samples/src/main/java/net/poksion/chorong/android/samples/domain/DbManager.java)

### A. View : SampleForPersistence
[SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) is the Activity and plays role for assembler and View.

#### a. Assembling

In Android, The Activity Component plays the assembler - initializing components, connecting each components and triggering the first job. SampleForPersistence acts exaclty same (except using [Assembler](chorong-core/src/main/java/net/poksion/chorong/android/module/Assembler.java))

The entry point of [ToolbarActivity](chorong-ui/src/main/java/net/poksion/chorong/android/ui/main/ToolbarActivity.java) (SampleForPersistence extends this) is onCreateContentView and [SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) overrides it

```java
@Override
protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
    ModuleFactory.assemble(this, new SampleForPersistenceAssembler(this, container));

    initCardView();
    presenter.addItems(buildSampleDbItem());
}
```

#### b. View (as part of MVP/Supervising Controller)

[SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) is kind of the view in [Superving Controller](https://martinfowler.com/eaaDev/SupervisingPresenter.html) and it plays less role for [Passive View](https://martinfowler.com/eaaDev/PassiveScreen.html). (You can find the diffrence of these in [here](https://martinfowler.com/eaaDev/uiArchs.html)). It has only two mehtod and ```isFinishing``` uses ```Acitivty.isFisinishg``` (do not need to re-implementing), so the importan method is only one : ```void showItems(List<DbItemModel> itemList)```

```java
@Override
public void showItems(List<DbItemModel> itemList) {
    for (DbItemModel model : itemList) {
        cardRecyclerView.addItem(
                dbItemViewModelUtil.makeViewModel(model),
                dbItemViewModelUtil.getViewBinder());
    }

    cardRecyclerView.notifyDataSetChanged();
}
```

The main UI compoent of SampleForPersistence is [FlatCardRecyclerView](chorong-ui/src/main/java/net/poksion/chorong/android/ui/card/FlatCardRecyclerView.java). [DbItemViewModelUtil](samples/src/main/java/net/poksion/chorong/android/samples/ui/DbItemViewModelUtil.java) is helper class for making ViewModel/ViewBinder used in FlatCardRecyclerView

See also : Module

### B. Presenter : SampleForPersistencePresenter

[SampleForPersistencePresenter](samples/src/main/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenter.java) does the business logic with realted dependeices. 

#### a. Related Dependencies

There are two main components:

 * [TaskRunner](chorong-core/src/main/java/net/poksion/chorong/android/task/TaskRunner.java)
 * [DbManager](samples/src/main/java/net/poksion/chorong/android/samples/domain/DbManager.java)

TaskRunner provides how running task - synchronous or asynchronous, running on executor, etc. I pefer to use [TaskRunnerAsyncShared](chorong-core/src/main/java/net/poksion/chorong/android/task/TaskRunnerAsyncShared.java) for IO task and [TaskRunnerSync](chorong-core/src/main/java/net/poksion/chorong/android/task/TaskRunnerSync.java) for testing (for easy to test, if the test does not need to run asynchronously)

[DbManager](samples/src/main/java/net/poksion/chorong/android/samples/domain/DbManager.java) is model that handles the database. chrong-android provides utils handing model like [ObjectStore](chorong-core/src/main/java/net/poksion/chorong/android/store/ObjectStore.java), [StoreAccessor](chorong-core/src/main/java/net/poksion/chorong/android/store/StoreAccessor.java) - DbManager is the application commponent that using [DatabaseProxyManager](chorong-core/src/main/java/net/poksion/chorong/android/store/persistence/DatabaseProxyManager.java) and DatabaseProxyManager basically uses ObjectStore/StoreAccessor.

#### b. Business logic

[SampleForPersistencePresenter](samples/src/main/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenter.java) does really simple logic : adding Items with DbManager and notifying result to View. You can find this in [test code](samples/src/test/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenterTest.java).

```java
public void test_add_item() {
    List<DbItemModel> items = new ArrayList<>();
    DbItemModel item = new DbItemModel();
    item.id = "dummy-id";
    item.name = "dummy-name";
    item.date = "dummy-date";

    items.add(item);

    presenter.addItems(items);
    verify(view, times(1)).showItems(captor.capture());

    List<DbItemModel> values = captor.getValue();
    assertThat(values.size()).isEqualTo(1);
    assertThat(values.get(0).id).isEqualTo("dummy-id");
}}
```

See also : Task

### C. Model : DbManager

See also : Store