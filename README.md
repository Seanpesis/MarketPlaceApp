Marketplace App 🛒


אפליקציית אנדרואיד לניהול רשימת מוצרים (Marketplace), שנבנתה כחלק ממטלת אמצע בקורס פיתוח אנדרואיד.
האפליקציה מאפשרת למשתמשים לנהל מלאי אישי, להוסיף פריטים עם תמונות, ולשמור את המידע מקומית על המכשיר בצורה מאובטחת ויעילה.


✨ פיצ'רים מרכזיים


ניהול מוצרים מלא (CRUD):


➕ הוספה: יצירת פריט חדש עם כותרת, מחיר, תיאור, טלפון ותמונה.


👁️ צפייה: הצגת רשימת פריטים ומסך פרטים מלא לכל מוצר.


✏️ עריכה: עדכון פרטים של מוצר קיים.


🗑️ מחיקה: הסרת מוצר מהרשימה (כולל דיאלוג אישור למניעת טעויות).


שמירת נתונים (Persistence): שימוש ב-Room Database לשמירת המידע באופן מקומי (המידע נשמר גם לאחר סגירת האפליקציה).


תמונות: אינטגרציה עם הגלריה לבחירת תמונות והצגתן באמצעות ספריית Glide.


ניווט: שימוש ב-Navigation Component למעבר חלק בין מסכים (רשימה -> פרטים -> עריכה).


חווית משתמש: תמיכה מלאה בסיבוב מסך (Screen Orientation) ללא איבוד נתונים, וממשק נקי ונוח.


🛠️ טכנולוגיות וארכיטקטורה


הפרויקט נכתב בשפת Kotlin ומבוסס על ארכיטקטורת MVVM המודרנית:


Architecture: MVVM (Model-View-ViewModel) with Repository pattern.


Database: Room (SQLite wrapper).


Concurrency: Coroutines & Flow (לביצוע פעולות ברקע).


Reactive UI: LiveData (לעדכון אוטומטי של הממשק בשינויי דאטה).


Navigation: Jetpack Navigation Component (Single Activity Architecture).


Image Loading: Glide.


Design: XML Layouts, Material Design Components.


📂 מבנה הפרויקט


data: מכיל את המודל (MarketItem), הגישה לדאטה-בייס (DAO) וה-Repository.


viewmodel: מכיל את הלוגיקה העסקית (MarketViewModel) המקשרת בין הדאטה לתצוגה.


ui (root package): מכיל את ה-Activity, ה-Fragments וה-Adapter של הרשימה.


🚀 איך להריץ את הפרויקט?


שכפלו את הריפו למחשב שלכם:


git clone [https://github.com/Seanpesis/MarketPlaceApp.git](https://github.com/Seanpesis/MarketPlaceApp.git)




פתחו את הפרויקט ב-Android Studio.


המתינו לסינכרון ה-Gradle (Sync Project).


הריצו את האפליקציה על אימולטור או מכשיר פיזי (מינימום SDK 24).


לצפייה באפליקציה:
https://www.youtube.com/shorts/Fp416FRaqDQ


