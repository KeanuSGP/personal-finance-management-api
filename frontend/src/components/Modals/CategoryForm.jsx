import Input from "../Input/Input"

const CategoryForm = ( {name, setName, color, setColor} ) => {
    return (
        <>
            <Input 
            type = "text"
            placeholder={"Nome da categoria"}
            value = {name}
            onChange={(e) => setName(e.target.value)}
            />

            <input
            type = "color"
            value = {color}
            onChange={(e) => setColor(e.target.value)}
            style={{width: '100%'}}
            />
        </>
    )
   
}

export default CategoryForm;