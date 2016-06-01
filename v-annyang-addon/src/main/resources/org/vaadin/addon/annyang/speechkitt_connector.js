/*global annyang*/
/*global SpeechKITT*/
window.org_vaadin_addon_annyang_SpeechKITT = function() {

    var me = this;

    if (annyang) {

        var updateCss = function() {
            var resource = me.getState().resources["css"];
            if (resource) {
                SpeechKITT.setStylesheet(me.translateVaadinUri(resource.uRL));
            }
        }
        var applyIfPresent = function(property, applier) {
            var value = me.getState()[property];
            if (me.getState().hasOwnProperty(property) && value) {
                applier(value);
            }
        };

        me.onStateChange = function() {
            updateCss();
            applyIfPresent("toggleLabelText", SpeechKITT.setToggleLabelText);
            applyIfPresent("instructionsText", SpeechKITT.setInstructionsText);
            applyIfPresent("sampleCommands", SpeechKITT.setSampleCommands);
            applyIfPresent("rememberStatusInterval", SpeechKITT.rememberStatus);
        };
        me.show = SpeechKITT.show;
        me.hide = SpeechKITT.hide;

        SpeechKITT.annyang();
        SpeechKITT.vroom();
        if (!me.getState().visible) {
            SpeechKITT.hide();
        }

    }

}