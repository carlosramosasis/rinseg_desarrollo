package rinseg.asistp.com.utils;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by OSequeiros on 22/02/2017.
 * Clase encargada de realizar las animaciones
 */

public class Animations {

    public static void animatedFabOnViewPager(final FloatingActionButton fab, final int[] icons,
                                       final Context context, final int position) {
        fab.clearAnimation();
        // Decrementar tamaño FAB
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.f, 1f, 0.f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(250);     // Duracion de la animación
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Cambiar el ícono del FAB
                fab.setImageDrawable(context.getResources().getDrawable(icons[position], null));

                // Incrementar tamaño FAB
                ScaleAnimation expand = new ScaleAnimation(0.f, 1f, 0.f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(200);     // Duración de la animación
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        fab.startAnimation(shrink);
    }
}
