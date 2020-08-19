diff --git a/app/src/main/java/fi/haltu/harrastuspassi/activities/HobbyDetailActivity.kt b/app/src/main/java/fi/haltu/harrastuspassi/activities/HobbyDetailActivity.kt
index a512403..abef869 100644
--- a/app/src/main/java/fi/haltu/harrastuspassi/activities/HobbyDetailActivity.kt
+++ b/app/src/main/java/fi/haltu/harrastuspassi/activities/HobbyDetailActivity.kt
@@ -9,6 +9,7 @@ import android.util.Log
 import android.view.LayoutInflater
 import android.view.Menu
 import android.view.MenuItem
+import android.view.View.GONE
 import android.widget.ImageView
 import android.widget.TableLayout
 import android.widget.TableRow
@@ -45,6 +46,7 @@ class HobbyDetailActivity : AppCompatActivity(), OnMapReadyCallback {
     private lateinit var descriptionTextView: TextView
     private lateinit var locationAddress: TextView
     private lateinit var locationZipCode: TextView
+    private lateinit var tableLayoutClock: TextView
     private var hobbyEventID: Int = 0
     private var locationReceived: Boolean = true
 
@@ -82,7 +84,7 @@ class HobbyDetailActivity : AppCompatActivity(), OnMapReadyCallback {
         descriptionTextView = findViewById(R.id.description_text)
         locationAddress = findViewById(R.id.promotion_location_address)
         locationZipCode = findViewById(R.id.promotion_location_zipcode)
-
+        tableLayoutClock = findViewById(R.id.tableLayoutClock)
         //Loads favorite id:s
         favorites = loadFavorites(this)
         if (favorites.contains(hobbyEventID)) {
@@ -262,10 +264,16 @@ class HobbyDetailActivity : AppCompatActivity(), OnMapReadyCallback {
                 idToWeekDay(hobbyEvent.startWeekday, this)
             Log.d("Table", formatDate(hobbyEvent.startDate))
             row.findViewById<TextView>(R.id.start_date).text = formatDate(hobbyEvent.startDate)
-            row.findViewById<TextView>(R.id.time).text =
-                convertToTimeRange(hobbyEvent.startTime, hobbyEvent.endTime)
-            tableLayout.addView(row)
-        }
+            Log.d("time", hobbyEvent.startTime)
+            if (hobbyEvent.startTime != "00:00:00" && hobbyEvent.endTime != "00:00:00") {
+                row.findViewById<TextView>(R.id.time).text =
+                    convertToTimeRange(hobbyEvent.startTime, hobbyEvent.endTime)
+            } else {
+                tableLayoutClock.visibility = GONE
+                row.findViewById<TextView>(R.id.time).visibility = GONE
+            }
+                tableLayout.addView(row)
+            }
 
         //DESCRIPTION
         descriptionTextView.setTextWithLinkSupport(hobbyEvents[0].hobby.description) {
diff --git a/app/src/main/res/layout/activity_hobby_detail.xml b/app/src/main/res/layout/activity_hobby_detail.xml
index a28d878..d9d5b26 100644
--- a/app/src/main/res/layout/activity_hobby_detail.xml
+++ b/app/src/main/res/layout/activity_hobby_detail.xml
@@ -187,6 +187,7 @@
                             android:layout_height="wrap_content">
 
                         <TextView
+                                android:id="@+id/tableLayoutClock"
                                 android:layout_width="24dp"
                                 android:layout_height="24dp"
                                 android:background="@drawable/clock_ic"
