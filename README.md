# Android Remote Control

The JAVA-based Android application acts as the wireless interface between the robot and its operator. The application implements Bluetooth communication protocols for establishing robust communication links with the Raspberry Pi module for continuous transmission of commands and information.

The application features automated and manual modes to control the state of the robot and incorporate interactive Graphical User Interface (GUI) controls for displaying the positions of the robot and obstacles on the maze in real-time.

<img src="https://github.com/guptajay/MDP_AndroidApp/blob/master/img/android_app.jpeg" width="350">

## Application Architecture
We have used some of the best practices highlighted by Google’s development team in their official documentation. We have incorporated Android native functionalities such as `Activities`, `Fragments`, `RecyclerViews`, `Broadcast Receivers`, and `Shared Preferences`. To summarise the layered architecture, all the UI components are driven by a static model that serves as ground truth for all data, where all logic is divided between Activities and Fragments. 

### Interactive 2D Maze
We used the magic of `RecyclerViews` in Android APIs to create the interactive 2D Maze. `RecyclerView` makes it easy to efficiently display large sets of data. We only supply the data and define items, the rest is handled by the `RecyclerViews` themselves.

In our case, we employ a `RecyclerView` to display the 2D Map, where each item corresponds to a cell in the 2D Map.  As soon as the application is launched, the `onCreateViewHolder` is called that initializes a `ViewHolder`, however, there is no data in the View yet. The critical function is `onBindViewHolder` that sets the contents for each item, a.k.a 2D Map cells in our case. For every cell, we fill a background colour, set a display text, and the text colour.

<img src="https://github.com/guptajay/MDP_AndroidApp/blob/master/img/snippet-1.png">

After constructing the 2D Map, we need to make it interactive, i.e. each cell of the map should be clickable and the user should be able to set the robot start point and fastest-path waypoints. To achieve the same, we set an `OnClickListener` for each cell, and add the functionality for each cell.

## Robot Driving & Obstacle Placement on the virtual Maze
To drive the robot or place an obstacle on the virtual maze, we change the properties of the unique cells in the maze. Due to the benefit of using `RecyclerViews`, we only refresh the cell that has been changed instead of the whole 2D map. Such an approach keeps our application from slowing down and prevents the 2D map to appear flashing every time a cell is updated in the map.

```adapter.notifyItemChanged(position);```

## Maintaining Data Persistence
One requirement of the application is to store data persistently (F1/F2 Strings), i.e. the data is retained even when the application is closed and shut-down. 

Such persistence perceived using another native Android API called `SharedPreferences`, which stores key-value data for relatively small sets of data. Please note that the context is set to `MODE_PRIVATE` such that the data can only be accessed by our app.

<img src="https://github.com/guptajay/MDP_AndroidApp/blob/master/img/snippet-2.png">

## Maintaining Bluetooth Connectivity
The Bluetooth interface is decomposed into two modules of Connection and Chat Service and their corresponding UI classes. The creates a modular code structure that leverages the design principle of low coupling and high cohesion. 

### Connection
The Bluetooth Connection Service Class is responsible for all functionalities pertaining to Bluetooth connection with 3 main components:

* **Accept Thread:** This function is used to listen for incoming Bluetooth connections and subsequently calls connect function to bond two devices. This thread is based on the discoverability of the Bluetooth device. 
* **Connect Thread:** Create a Bluetooth connection between the socket and the device and initiate bonding. This function also updates the connection status using a Local broadcast receiver to track the status of the Bluetooth connection.
* **Start Bluetooth Connection:** Function to initialize RFCOMM Bluetooth connection and initiate bonding by invoking the connect thread with the UUID of the two devices.
 
> This list is not exhaustive and internally calls helper functions.

## Bluetooth Chat
The Bluetooth connection service uses `LocalBroadcastManager` to listen for incoming messages and passes the context to the chat service for handing the incoming message for parsing and handling the incoming messages. This service class implements a `messageDelivery` function to map a reference string to the corresponding function (e.g., update grid, add obstacle etc.) by invoking a Broadcast Receiver. This class also handles outgoing messages using the `sendMessage` function which internally calls the connection service object to parse and write the byte converted string over the serial communication link.

## Robust Connectivity
The Android remote control provides robust and persistent connectivity between the devices. The connection service class implements a Runnable interface to ensure automatic reconnection in the event of a breakdown. This is achieved by tracking the Bluetooth state change by executing the listener associated with the Bluetooth state change. This allows us to asynchronously run the reconnection upon interruption. 

<img src="https://github.com/guptajay/MDP_AndroidApp/blob/master/img/snippet-3.jpeg">

## Extension Beyond the Basics
Our application implements a voice assistant that can be used to manually control the robot in a hands-free mode. This is achieved by using Google’s speech recognizer modules and language models. The implemented method maps the input to a key stored inside a static `HashMap` and the corresponding instructions are executed by the robot (e.g., Move Forward command is mapped to instruction `robot.moveForward()`). 
