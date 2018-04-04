package sp18.cs370.seekingblood;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import static java.lang.Math.abs;

public class levelActivity extends AppCompatActivity implements
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private Hero hero;                  // Main Character
    private Handler moveHandler;        // Handler used in horizontal movement
    private Handler jumpHandler;        // Handler used in vertical movement
    private Handler jumpTimerHandler;   // Handler used in complex jumping
    private int region;                 // Integer that refers to a region of the screen
    private int width;                  // Integer that holds the width of the screen
    private int height;                 // Integer that holds the height of the screen
    private GestureDetector gestureDetector; // Used to detect unique gestures
    private Rect sprintRegionR;         // Sprint Right
    private Rect walkRegionR;           // Walk Right
    private Rect walkRegionL;           // Walk Left
    private Rect sprintRegionL;         // Sprint Left
    private Rect currentRegion;         // Region used in movement
    private Rect movementRegion;        // Region that contains all movement sub-regions
    private Rect actionRegion;          // Region that will capture actions unrelated to movement
    private Rect floorRegion;           // Region that acts as a floor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        // Forces screen into landscape orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Get screen size in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        // Create a hero, set their location, and other statuses
        hero = new Hero();
        hero.setHeroView((ImageView)findViewById(R.id.characterID));
        hero.setHeroViewX(width / 2);
        hero.setHeroViewY(height / 2);
        hero.setyVelocity(Constants.defaultJumpVelocity);
        hero.setFacingLeft(false);
        // Define the region boundaries
        sprintRegionR = new Rect(0, 0, width / 6, height / 4);
        walkRegionR = new Rect(0, height / 4, width / 6, height / 2);
        walkRegionL = new Rect(0, height / 2, width / 6, (3 * height) / 4);
        sprintRegionL = new Rect(0, (3 * height) / 4, width / 6, height);
        movementRegion = new Rect(0, 0, width / 6, height);
        actionRegion = new Rect(width / 6, 0, width, height);
        floorRegion = new Rect(0, height / 2, width, height);
        gestureDetector = new GestureDetector(this);
        // Map the View
        View mainView = findViewById(R.id.baseViewID);
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                region = GetRegionPressed((int)event.getX(), (int)event.getY());
                if(event.getPointerCount() == 2) {
                    System.out.println("Detecting 2 fingers!");
                    for(int i = 0; i < event.getPointerCount(); ++i) {
                        float x = event.getX(i);
                        float y = event.getY(i);
                        if(movementRegion.contains((int)x, (int)y)) {
                            region = GetRegionPressed((int) x, (int) y);
                            if (jumpTimerHandler != null) {
                                return true;
                            }
                            jumpTimerHandler = new Handler();
                            jumpTimerHandler.postDelayed(jumpTimer, 500);
                            if (region == 1) {
                                hero.setxVelocity(Constants.heroRunningSpeed);
                                System.out.println("X Velocity was set!");
                            } else if (region == 2) {
                                hero.setxVelocity(Constants.heroWalkingSpeed);
                                System.out.println("X Velocity was set!");
                            } else if (region == 3) {
                                hero.setxVelocity(Constants.heroWalkingSpeed * -1);
                                System.out.println("X Velocity was set!");
                            } else if (region == 4) {
                                hero.setxVelocity(Constants.heroRunningSpeed * -1);
                                System.out.println("X Velocity was set!");
                            } else {
                                return gestureDetector.onTouchEvent(event);
                            }
                        }
                    }
                    return true;
                } else if(region == 5) {
                    return gestureDetector.onTouchEvent(event);
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: // Initial Press
                            v.performClick();
                            if (moveHandler != null)
                                return true;
                            moveHandler = new Handler();
                            moveHandler.postDelayed(moveAction, 0);
                            break;
                        case MotionEvent.ACTION_MOVE: // While held and finger isn't steady
                            if (moveHandler == null)
                                return true;
                            else if (region == 1)
                                currentRegion = sprintRegionR;
                            else if (region == 2)
                                currentRegion = walkRegionR;
                            else if (region == 3)
                                currentRegion = walkRegionL;
                            else if (region == 4)
                                currentRegion = sprintRegionL;
                            if (!currentRegion.contains((int) event.getX(), (int) event.getY())) {
                                moveHandler.removeCallbacks(moveAction);
                                moveHandler = null;
                            }
                            break;
                        case MotionEvent.ACTION_UP: // Touch released
                            if (moveHandler == null)
                                return true;
                            moveHandler.removeCallbacks(moveAction);
                            moveHandler = null;
                            break;
                    }
                    return true;
                }
            }
            Runnable moveAction = new Runnable() { // Runnable that repeatedly calls move functions
                @Override
                public void run() {
                    if(region == 1)
                        hero.SprintRight(movementRegion, width);
                    else if(region == 2)
                        hero.WalkRight(movementRegion, width);
                    else if(region == 3)
                        hero.WalkLeft(movementRegion, width);
                    else if(region == 4)
                        hero.SprintLeft(movementRegion, width);
                    moveHandler.postDelayed(this, 20);
                }
            };
            Runnable jumpTimer = new Runnable() { // Runnable that waits 1 second
                @Override
                public void run() {
                    if(hero.isOnGround()) {
                        hero.setxVelocity(0);
                    }
                    jumpTimerHandler.removeCallbacks(jumpTimer);
                    jumpTimerHandler = null;
                    System.out.println("Jump Timer Resolved!");
                }
            };
        });
    }

    protected int GetRegionPressed(int x, int y) {
        if(sprintRegionR.contains(x, y)) {
            return 1;
        } else if(walkRegionR.contains(x, y)) {
            return 2;
        } else if(walkRegionL.contains(x, y)) {
            return 3;
        } else if(sprintRegionL.contains(x, y)) {
            return 4;
        } else if(actionRegion.contains(x, y)) {
            return 5;
        } else
            return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        // If there is a larger change in X, the swipe must be horizontal.
        // If there is a larger change in Y, the swipe must be vertical.
        int deltaX = abs((int)(event1.getX() - event2.getX()));
        int deltaY = abs((int)(event1.getY() - event2.getY()));
        if(deltaY > deltaX) { // Swipe is vertical
            if(event1.getY() > event2.getY()) {
                System.out.println("Swipe up detected!");
                if(hero.isOnGround()) {
                    if (jumpHandler != null)
                        return true;
                    jumpHandler = new Handler();
                    jumpHandler.postDelayed(jumpAction, 0);
                }
            }
            else
                System.out.println("Swipe down detected!");
        } else { // Swipe must be horizontal
            if(event1.getX() > event2.getX())
                System.out.println("Swipe left detected!");
            else
                System.out.println("Swipe right detected!");
        }
        return true;
    }
    Runnable jumpAction = new Runnable() {
        @Override
        public void run() {
            hero.Jump(floorRegion, height);
            if(!hero.isFinishedJump())
                jumpHandler.postDelayed(jumpAction, 20);
            else {
                hero.setFinishedJump(false);
                jumpHandler.removeCallbacks(jumpAction);
                jumpHandler = null;
            }
        }
    };

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {

        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {

        return true;
    }
}
