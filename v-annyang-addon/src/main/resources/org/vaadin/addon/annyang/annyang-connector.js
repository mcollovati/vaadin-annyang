window.org_vaadin_addon_annyang_Annyang = function() {
    var me = this;
    var emptyFn = function() {};
    var callbackRegistry = {};

    var opts = {};
    opts.autoRestart = me.getState().autoRestart;

    me.onStateChange = function() {
        //me.annyang.setLanguage(me.getState().lang);
        opts.autoRestart = me.getState().autoRestart;
    }
    me.onUnregister = function() {
        me.annyang.stop();
        me.annyang.removeCommands();
        me.annyang.removeCallback();
        delete callbackRegistry;
    };
    me.onUnsupported = function() {
        me.fireStatusChanged('unsupported');
        for (callbackName in callbackRegistry) {
            if (callbackRegistry[callbackName].type == 'unsupported') {
                callbackRegistry[callbackName].cb();
            }
        }
    };

    me.annyang = annyang || {
        start: me.onUnsupported,
        abort: emptyFn,
        pause: emptyFn,
        resume: emptyFn,
        addCommands: emptyFn,
        removeCommands: emptyFn,
        addCallback: emptyFn,
        removeCallback: emptyFn,
        isListening: function() {
            return false;
        },
        setLanguage: emptyFn,
        debug: emptyFn
    };






    me.annyang.addCallback('start', function() {
        console.log("start callback, fire status change");
        me.fireStatusChanged('started');
    });
    me.annyang.addCallback('end', function() {
        console.log("end callback, fire status change");
        me.fireStatusChanged('stopped');
    });

    //me.annyang.debug(true);

    me.debug = me.annyang.debug;
    me.setLanguage = function() {
        me.annyang.setLanguage(me.getState().lang);
        //if (me.annyang.isListening()) {
            me.abort();
            me.start();
        //}
    };
    me.start = function() {
        me.annyang.start(opts);
    };
    me.abort = me.annyang.abort;
    me.pause = function() {
        me.annyang.pause();
        console.log("pause, fire status change");
        me.fireStatusChanged('paused');
        console.log("pause 2, fire status change");
    }
    me.resume = function() {
        me.annyang.resume();
        if (me.annyang.isListening()) {
            me.fireStatusChanged('started');
        }
        console.log("resume, NO fire status change");
    }
    me.addCommand = function(phrase, fn) {
        var cmd = {};
        cmd[phrase] = me[fn];
        me.annyang.addCommands(cmd);
    };
    me.removeCommand = function(phrase, fn) {
        delete me[fn];
        me.annyang.removeCommands(phrase);
    };
    me.addCallback = function(type, callbackName) {
        console.log("MCK registering callback " + callbackName + " for type " + type);
        if (me.hasOwnProperty(callbackName)) {
            var fn = function() { me[callbackName].apply(this, Array.prototype.slice.call(arguments)); };
            callbackRegistry[callbackName] = { cb: fn, type: type };
            me.annyang.addCallback(type, fn);
            console.log("MCK callback " + callbackName + " for type " + type + " registered");
        }
    };
    me.removeCallback = function(type, callbackName) {
        console.log("MCK removing callback " + callbackName + " for type " + type);
        var fn = callbackRegistry[callbackName].cb;
        me.annyang.removeCallback(type, fn);
        delete callbackRegistry[callbackName];
        delete me[callbackName];
    };
}