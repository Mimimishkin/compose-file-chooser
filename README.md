This version looks awful, so I decided to rewrite everything from scratch with material 3. I added new features and improved the appearance 100 times over. And... Deleted everything. In the next couple of months I'll make a kotlin-multiplatform library which will able to open the native file chooser 

# Compose file chooser
[![](https://jitpack.io/v/Mimimishkin/compose-file-chooser.svg)](https://jitpack.io/#Mimimishkin/compose-file-chooser)
### Short description
It's simple file chooser in material design that was designed just for my personal needs.

It supports:
1. file selection
2. directory selection
3. multiselection
4. table and grid representation
5. actions like "new folder", "delete", "copy/paste"
6. filters
7. unfortunately **not** selection by drag

### Sample
Sourse code:

    fun main() = singleWindowApplication {
        MaterialTheme {
            val dialogState = rememberChooserDialogState()
            val scope = rememberCoroutineScope()

            Button(onClick = {
                scope.launch {
                    println(dialogState.choose())
                }
            }) {
                Text("Choose files")
            }
        }
    }

Result:

![Sample](screenshots/sample.png)
