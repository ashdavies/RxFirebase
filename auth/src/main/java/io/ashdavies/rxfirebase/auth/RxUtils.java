package io.ashdavies.rxfirebase.auth;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

public final class RxUtils {
  private RxUtils() {
    throw new IllegalArgumentException("No instances");
  }

  public static <T> Observable<T> from(final Task<T> task) {
    return from(task, AsyncEmitter.BackpressureMode.BUFFER);
  }

  public static <T> Observable<T> from(final Task<T> task, final AsyncEmitter.BackpressureMode mode) {
    return Observable.fromAsync(new Action1<AsyncEmitter<T>>() {
      @Override public void call(final AsyncEmitter<T> emitter) {
        task.addOnCompleteListener(new OnCompleteListener<T>() {
          @Override public void onComplete(@NonNull Task<T> task) {
            if (task.isSuccessful()) {
              emitter.onNext(task.getResult());
              emitter.onCompleted();
            }
          }
        });

        task.addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception exception) {
            emitter.onError(exception);
          }
        });
      }
    }, mode);
  }
}
