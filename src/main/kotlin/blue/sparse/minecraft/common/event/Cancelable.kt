package blue.sparse.minecraft.common.event

interface Cancelable {
	var canceled: Boolean

	fun cancel() {
		canceled = true
	}

	fun uncancel() {
		canceled = false
	}
}