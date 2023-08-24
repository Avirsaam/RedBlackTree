/*
Использованы материалы:
Графический симулятор КЧД: https://www.cs.usfca.edu/~galles/visualization/RedBlack.html
https://www.happycoders.eu/algorithms/binary-search-tree-java/
https://www.happycoders.eu/algorithms/red-black-tree-java/
 */


package RedBlackTree;

public class RedBlackTree {
    Node root;

    public boolean contains(int value){
        Node node = findNode(root, value);
        return node != null;
    }

    private Node findNode(Node node, int value){
        if (node.value == value){
            return node;
        } else {
            if (node.value > value) {
                if (node.left != null){
                    return findNode(node.left, value);
                } else {
                    return null;
                }
            } else {
                if (node.right != null){
                    return findNode(node.right, value);
                } else {
                    return null;
                }
            }
        }
    }

    public void insertNode(int value){
        Node newNode = new Node(value);

        if (root == null){
            root = newNode;
            return;
        }

        Node node = root;
        Node parent = null;

        while (node != null){
            parent = node;
            if (value == node.value){ return; }
            else if (value < node.value) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        if (parent != null){
            newNode.parent = parent;
            if (value < parent.value){
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
        }
        balanceRedBlackTreeAfterInsertion(newNode);
    }

    private void balanceRedBlackTreeAfterInsertion(Node node){
        Node parent = node.parent;

        //Случай 1: Папы нет, и мы достигли корня, конец рекурсии, принудительно меняем
        //цвет корня на черный (так проще, чтобы избежать дополнительной проверки
        //ниже закомментировано
        if(parent == null){
            node.isRed = false; //принудительно
            return;
        }
        //папа черного цвета, ничего делать не нужно, выходим
        if (parent.isRed != true){
            return;
        }
        //Ниже может быть только папа красного цвета

        //дедушка обязательно есть, иначе мы бы уже вышли из программы,
        // так как нет дедушки только если папа - корень,
        // а он у нас черный принудительно (см.выше), а если папа черный, то выходим (выше)
        Node grandparent = parent.parent;

        // Случай 2:
        // Если нет дедушки, значит что папа - корень
        // перекрашиваем корень в черный
        if (grandparent == null) {
            // As this method is only called on red nodes (either on newly inserted ones - or -
            // recursively on red grandparents), all we have to do is to recolor the root black.
            parent.isRed = false;
            return;
        }

        //На данном этапе нам надо знать про дядю. Если он null, то цвет у него черный
        Node uncle;
        if (grandparent.left == parent.parent){
            uncle = grandparent.right;
        } else {
            uncle = grandparent.left;
        }

        //Случай 3 - если дядя красный и папа красный, то применяем смену цвета
        //дедушку в красный, а дядю и папу в черный
        if (uncle != null && uncle.isRed){
            grandparent.isRed = true;
            parent.isRed = false;
            uncle.isRed = false;
            //вызываем рекурсивно на дедушку, потому что он стал красным
            balanceRedBlackTreeAfterInsertion(grandparent);
        }
        //или если папа левый ребенок дедушки
        else if (parent == grandparent.left){
            //Случай 4a: Дядя черный и эта нода есть "левый внутренний внук" своего деда
            // (смотрит внутрь, образует треугольник между дед-папа-внук, лево->право
            if (node == parent.right){
                rotateLeft(parent);
                //после разворота папа эта нода становится на место папы
                //цвет поменяем позже
                parent = node;
            }

            //Случай 5а: Дядя черный и эта нода левая->левая,
            // т.н. "левый наружний внук" своего деда
            rotateRight(grandparent);

            parent.isRed = false;
            grandparent.isRed = true;
        } else {
            //Случай 4b: Дядя черный и эта нода есть "правый внутренний внук" своего деда
            // (смотрит внутрь, образует треугольник между дед-папа-внук, вправо->влево
            if (node == parent.right){
                rotateRight(parent);
                //после разворота папа эта нода становится на место папы
                //цвет поменяем позже
                parent = node;
            }

            //Случай 5b: Дядя черный и эта нода правая->правая,
            // т.н. "правый наружний внук" своего деда
            rotateLeft(grandparent);

            parent.isRed = false;
            grandparent.isRed = true;
        }
    }

    //Вспомогательные функции для балансировки после вставки
    private Node getUncle(Node parent){
        Node grandparent = parent.parent;
        if (grandparent.left == parent) return grandparent.right;
        else return grandparent.left;
    }

    private void rotateRight(Node node) {
        Node parent = node.parent;
        Node leftChild = node.left;

        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.right = node;
        node.parent = leftChild;

        replaceParentsChild(parent, node, leftChild);
    }

    private void rotateLeft(Node node) {
        Node parent = node.parent;
        Node rightChild = node.right;

        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.left = node;
        node.parent = rightChild;

        replaceParentsChild(parent, node, rightChild);
    }

    private void replaceParentsChild(Node parent, Node oldChild, Node newChild) {
        if (parent == null) {
            root = newChild;
        } else if (parent.left == oldChild) {
            parent.left = newChild;
        } else if (parent.right == oldChild) {
            parent.right = newChild;
        } else {
            throw new IllegalStateException("Node is not a child of its parent");
        }

        if (newChild != null) {
            newChild.parent = parent;
        }
    }


    private class Node{
        Node left;
        Node right;
        boolean isRed;
        int value;
        Node parent;

        Node(int value){
            this.value = value;
            this.isRed = true;
            this.parent = null;
        }
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        appendNodeToStringRecursive(root, builder);
        return builder.toString();
    }

    private void appendNodeToStringRecursive(Node node, StringBuilder builder) {
        appendNodeToString(node, builder);
        if (node.left != null) {
            builder.append(" L{");
            appendNodeToStringRecursive(node.left, builder);
            builder.append('}');
        }
        if (node.right != null) {
            builder.append(" R{");
            appendNodeToStringRecursive(node.right, builder);
            builder.append('}');
        }
    }

    protected void appendNodeToString(Node node, StringBuilder builder) {
        builder.append(node.value).append(node.isRed == true ? "[R]" : "[B]");
    }
}
