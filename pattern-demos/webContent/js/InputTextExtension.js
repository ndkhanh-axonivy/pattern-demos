PrimeFaces.widget.InputText.prototype.init = function(cfg) {
        this.cfg = cfg;
		this.jq = $(PrimeFaces.escapeClientId(this.cfg.id));
        PrimeFaces.skinInput(this.jq);

        //Counter
        if(this.cfg.counter) {
            this.counter = this.cfg.counter ? $(PrimeFaces.escapeClientId(this.cfg.counter)) : null;
            this.cfg.counterTemplate = this.cfg.counterTemplate||'{0}';
            this.updateCounter();

            if(this.counter) {
                var $this = this;
                this.jq.on('input.inputtext-counter', function(e) {
                    $this.updateCounter();
                });
            }
        }
	
		 this.normalizeNewlines(this.jq.val());
		 var $this = this;
         this.jq.on('keyup.inputtextarea-maxlength', function(e) {
         	if(e.currentTarget.maxLength > 0){
            while(byteSize(e.currentTarget.value) > e.currentTarget.maxLength) {
            e.currentTarget.value = e.currentTarget.value.slice(0,-1);
            }
             }
        });
       
    }