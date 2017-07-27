# Chorong Android

[![Travis branch](https://img.shields.io/travis/poksion/chorong-android/master.svg)](https://travis-ci.org/poksion/chorong-android)
[![Codecov](https://img.shields.io/codecov/c/github/poksion/chorong-android.svg)](https://codecov.io/gh/poksion/chorong-android)

Chorong Android is a little Android utility providing MVP development modules, UI component, Google API wrappers, and extras.

## Installing

To use only core things (Module, Task, ...)

```groovy
dependencies {
    compile 'net.poksion:chorong-android-core:0.1.7'
}
```

If you want use UI stuff (ToolbarActivity, FlatCardRecyclerView, ...), append 'ui' module

```groovy
dependencies {
    compile 'net.poksion:chorong-android-core:0.1.7'
    compile 'net.poksion:chorong-android-ui:0.1.7'
}
```

## Sample MVP
[SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) is an entry point to show how implement activity with MVP pattern using chorong-android. It consists of 3 major parts : [SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java), [SampleForPersistencePresenter](samples/src/main/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenter.java) and [SampleItemRepository](samples/src/main/java/net/poksion/chorong/android/samples/domain/SampleItemRepository.java)

### View : SampleForPersistence
[SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) is the Activity and plays role for assembler and View.

#### Assembling

In Android, The Activity Component plays the assembler - initializing components, connecting each components and triggering the first job. SampleForPersistence acts exaclty same (except using [Assembler](chorong-core/src/main/java/net/poksion/chorong/android/module/Assembler.java))

The entry point of [ToolbarActivity](chorong-ui/src/main/java/net/poksion/chorong/android/ui/main/ToolbarActivity.java) (SampleForPersistence extends this) is onCreateContentView and [SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) overrides it

```java
@Override
protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
    ModuleFactory.assemble(SampleForPersistence.class, this, new SampleForPersistenceAssembler(this, container));

    initCardView();
    presenter.addItems(buildSampleDbItem());
}
```

#### View (as part of MVP/Supervising Controller)

[SampleForPersistence](samples/src/main/java/net/poksion/chorong/android/samples/SampleForPersistence.java) is kind of the view in [Superving Controller](https://martinfowler.com/eaaDev/SupervisingPresenter.html) and it plays less role for [Passive View](https://martinfowler.com/eaaDev/PassiveScreen.html). (You can find the diffrence of these in [here](https://martinfowler.com/eaaDev/uiArchs.html)). It has only two mehtod and ```isFinishing``` uses ```Acitivty.isFisinishg``` (do not need to re-implementing), so the importan method is only one : ```void showItems(List<DbItemModel> itemList)```

```java
@Override
public void showItems(List<SampleItem> itemList) {
    for (SampleItem model : itemList) {
        cardRecyclerView.addItem(
                sampleItemViewModelUtil.makeViewModel(model),
                sampleItemViewModelUtil.makeViewBinder(new SampleItemClickHandler() {
                    @Override
                    public void onItemClick(String id) {
                        presenter.reloadItem(id);
                    }
                }));
    }

    cardRecyclerView.notifyDataSetChanged();
}
```

The main UI compoent of SampleForPersistence is [FlatCardRecyclerView](chorong-ui/src/main/java/net/poksion/chorong/android/ui/card/FlatCardRecyclerView.java). [SampleItemViewModelUtil](samples/src/main/java/net/poksion/chorong/android/samples/ui/SampleItemViewModelUtil.java) is helper class for making ViewModel/ViewBinder used in FlatCardRecyclerView

See also : Module

### Presenter : SampleForPersistencePresenter

[SampleForPersistencePresenter](samples/src/main/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenter.java) does the business logic with realted dependeices. 

#### Related Dependencies

There are two main depending components:

 * [TaskRunner](chorong-core/src/main/java/net/poksion/chorong/android/task/TaskRunner.java)
 * [SampleItemRepository](samples/src/main/java/net/poksion/chorong/android/samples/domain/SampleItemRepository.java)

TaskRunner provides how running tasks - synchronous or asynchronous, running on executor, etc. We prefer to use [TaskRunnerAsyncShared](chorong-core/src/main/java/net/poksion/chorong/android/task/TaskRunnerAsyncShared.java) for IO task and [TaskRunnerSync](chorong-core/src/main/java/net/poksion/chorong/android/task/TaskRunnerSync.java) for testing (for easy to test, if the test does not need to run asynchronously)

[SampleItemRepository](samples/src/main/java/net/poksion/chorong/android/samples/domain/SampleItemRepository.java) is model that handles the database. chrong-android provides [ObjectStore](chorong-core/src/main/java/net/poksion/chorong/android/store/ObjectStore.java), [StoreAccessor](chorong-core/src/main/java/net/poksion/chorong/android/store/StoreAccessor.java) - SampleItemRepository is the application commponent that using [DatabaseProxyManager](chorong-core/src/main/java/net/poksion/chorong/android/store/persistence/DatabaseProxyManager.java) and DatabaseProxyManager basically uses ObjectStore/StoreAccessor.

#### Business logic

[SampleForPersistencePresenter](samples/src/main/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenter.java) does really simple logic : adding Items with SampleItemRepository and notifying result to View. You can find this in [test code](samples/src/test/java/net/poksion/chorong/android/samples/presenter/SampleForPersistencePresenterTest.java).

```java
@Test
public void view_should_show_items_stored_on_repository() {
    List<SampleItem> items = new ArrayList<>();
    SampleItem item = new SampleItem();
    item.id = "dummy-id";
    item.name = "dummy-name";
    item.date = "dummy-date";

    items.add(item);
    sampleItemRepository.storeAll(items);

    presenter.readDb();
    verify(view, times(1)).showItems(captor.capture());

    List<SampleItem> values = captor.getValue();
    assertThat(values.size()).isEqualTo(1);
    assertThat(values.get(0).id).isEqualTo("dummy-id");
}
```

See also : Task

### Model : SampleItemRepository

[SampleItemRepository](samples/src/main/java/net/poksion/chorong/android/samples/domain/SampleItemRepository.java) is the core model in this sample. It uses [DatabaseProxyManager](chorong-core/src/main/java/net/poksion/chorong/android/store/persistence/DatabaseProxyManager.java) to save entity to DB.

DatabaseProxyManager manages persistence proxies that read/write data from DB when realted [StoreAccessor](chorong-core/src/main/java/net/poksion/chorong/android/store/StoreAccessor.java) is called with getter/setter.

See also : Store
