package com.rxjava.app.packt.chapter02;

import io.reactivex.rxjava3.core.Observable;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Ch02_13 extends Application {
  private Observable<Boolean> valuesOf(final ObservableValue<Boolean> observableValue) {
    return Observable.create(emitter -> {
      emitter.onNext(observableValue.getValue());
      final ChangeListener<Boolean> changeListener = (value, prev, current) -> emitter.onNext(current);
      observableValue.addListener(changeListener);
    });
  }
  @Override
  public void start(Stage stage) {
    ToggleButton toggleButton = new ToggleButton("ToggleMe");
    Label label = new Label();
    Observable<Boolean> selectedStates = valuesOf(toggleButton.selectedProperty());
    selectedStates.map(selected -> selected ? "DOWN" : "UP").subscribe(label::setText);
    VBox vBox = new VBox(toggleButton);
    stage.setScene(new Scene(vBox));
    stage.show();
  }
  public static void main(String[] args) {
    launch();
  }
}
