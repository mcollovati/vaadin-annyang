window.org_vaadin_addon_annyang_demo_Twitter = function() {
    /*
    if (!window.twttr) {
        window.twttr = (function(d, s, id) {
          var js, fjs = d.getElementsByTagName(s)[0],
            t = window.twttr || {};
          if (d.getElementById(id)) return t;
          js = d.createElement(s);
          js.id = id;
          js.src = "https://platform.twitter.com/widgets.js";
          fjs.parentNode.insertBefore(js, fjs);

          t._e = [];
          t.ready = function(f) {
            t._e.push(f);
          };

          return t;
        }(document, "script", "twitter-wjs"));
        console.log("Create window.twttr", window.twttr);
    }
    */
    var me = this;
    me.refresh = function() {
        window.twttr.widgets.load(me.getElement());
    };
    me.createTimeline = function(id, width, height) {
        window.twttr.widgets.createTimeline(id, me.getElement(), {
            width: width,
            height: height
        });
    };

    //me.refresh();

};