# Promise

#Usage

(new Promise())
    .then((Promise.Task<Void, Integer>) args -> 100)
    .then((Promise.Task<Integer, String>) args -> String.valueOf(args))
    .then((Promise.Task<String, Boolean>) args -> {
        Thread.sleep(1000);
        return Integer.valueOf(args) == 100;
    })
    .catchError((error, task) -> System.out.println("Got error: " + error.getMessage()));
